window.hotelApp = (() => {
    const storageKeys = {
        token: 'hotelBooking.token',
        user: 'hotelBooking.user'
    };

    const getToken = () => localStorage.getItem(storageKeys.token);
    const getUser = () => {
        const raw = localStorage.getItem(storageKeys.user);
        return raw ? JSON.parse(raw) : null;
    };

    const setAuth = (payload) => {
        localStorage.setItem(storageKeys.token, payload.token);
        localStorage.setItem(storageKeys.user, JSON.stringify(payload));
        syncShell();
    };

    const logout = () => {
        localStorage.removeItem(storageKeys.token);
        localStorage.removeItem(storageKeys.user);
        syncShell();
        window.location.href = '/login';
    };

    const syncShell = () => {
        const user = getUser();
        document.querySelectorAll('.guest-only').forEach((element) => element.classList.toggle('d-none', !!user));
        document.querySelectorAll('.auth-only').forEach((element) => element.classList.toggle('d-none', !user));
        document.querySelectorAll('.admin-only').forEach((element) => {
            const show = !!user && user.role === 'ROLE_ADMIN';
            element.classList.toggle('d-none', !show);
        });

        if (user) {
            const nameEl = document.getElementById('user-name-display');
            const fullnameEl = document.getElementById('profile-fullname');
            const emailEl = document.getElementById('profile-email');
            const name = user.fullName || user.username || user.email || 'User';
            if (nameEl) nameEl.textContent = name;
            if (fullnameEl) fullnameEl.textContent = name;
            if (emailEl) emailEl.textContent = user.email || '';
        }
    };

    const requireAuth = () => {
        if (!getToken()) {
            const currentUrl = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = `/login?next=${currentUrl}`;
            return false;
        }
        return true;
    };

    const requireRole = (role) => {
        if (!requireAuth()) return;
        const user = getUser();
        if (!user || user.role !== role) {
            window.location.href = '/dashboard';
            throw new Error('Access denied');
        }
    };

    const api = async (url, options = {}) => {
        const headers = new Headers(options.headers || {});
        if (!headers.has('Content-Type') && options.body) {
            headers.set('Content-Type', 'application/json');
        }
        const token = getToken();
        if (token) {
            headers.set('Authorization', `Bearer ${token}`);
        }

        const response = await fetch(url, { ...options, headers });
        if (!response.ok) {
            if (response.status === 401) {
                logout();
                return;
            }
            let payload;
            try {
                const text = await response.text();
                try {
                    payload = JSON.parse(text);
                } catch (e) {
                    payload = { message: `Request failed with status ${response.status}: ${response.statusText || text || 'Internal Error'}` };
                }
            } catch (error) {
                payload = { message: `Request failed with status ${response.status}` };
            }
            throw new Error(payload.message || payload.details || 'Request failed');
        }

        if (response.status === 204) {
            return null;
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return response.text();
    };

    const formatCurrency = (amount) => new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
    }).format(Number(amount || 0));

    const formatDate = (value) => {
        if (!value) {
            return '-';
        }
        return new Date(value).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
    };

    const toStatusClass = (status) => (status || '').toLowerCase();

    document.addEventListener('DOMContentLoaded', syncShell);

    const locations = {
        "Maharashtra": ["Mumbai", "Pune", "Nagpur", "Nashik", "Aurangabad", "Mahabaleshwar", "Lonavala"],
        "Karnataka": ["Bangalore", "Mysore", "Mangalore", "Hubli", "Coorg", "Gokarna"],
        "Tamil Nadu": ["Chennai", "Coimbatore", "Madurai", "Ooty", "Kodaikanal"],
        "Kerala": ["Kochi", "Thiruvananthapuram", "Munnar", "Wayanad", "Alleppey", "Kozhikode"],
        "Rajasthan": ["Jaipur", "Udaipur", "Jodhpur", "Jaisalmer", "Pushkar", "Mount Abu"],
        "Delhi": ["New Delhi", "Dwarka", "Aerocity"],
        "Goa": ["Panaji", "Candolim", "Calangute", "Baga", "Anjuna", "South Goa"],
        "West Bengal": ["Kolkata", "Darjeeling", "Siliguri", "Digha"],
        "Gujarat": ["Ahmedabad", "Surat", "Vadodara", "Rajkot"],
        "Himachal Pradesh": ["Shimla", "Manali", "Dharamshala", "Dalhousie", "Kasauli"],
        "Uttarakhand": ["Dehradun", "Mussoorie", "Rishikesh", "Nainital", "Haridwar"]
    };

    const getLocations = () => locations;

    const buildLocationDropdown = (inputId, dropdownId) => {
        const input = document.getElementById(inputId);
        const dropdown = document.getElementById(dropdownId);
        if (!input || !dropdown) return;

        const renderList = (filterText = '') => {
            let html = '';
            const lowerFilter = filterText.toLowerCase();
            for (const [state, cities] of Object.entries(locations)) {
                const matchedCities = cities.filter(c => c.toLowerCase().includes(lowerFilter) || state.toLowerCase().includes(lowerFilter));
                if (matchedCities.length > 0) {
                    html += `<div class="px-3 py-1 bg-light text-muted small fw-bold">${state}</div>`;
                    matchedCities.forEach(city => {
                        html += `<div class="p-2 cursor-pointer location-item hover-bg-light" style="cursor: pointer;" onclick="document.getElementById('${inputId}').value = '${city}'; document.getElementById('${dropdownId}').classList.add('d-none');">
                                    <div class="fw-bold">${city}</div>
                                    <div class="small text-muted">${state}, India</div>
                                 </div>`;
                    });
                }
            }
            dropdown.innerHTML = html || '<div class="p-3 text-muted text-center small">No matches found</div>';
        };

        input.addEventListener('focus', () => {
            renderList(input.value);
            dropdown.classList.remove('d-none');
        });

        input.addEventListener('input', (e) => {
            renderList(e.target.value);
            dropdown.classList.remove('d-none');
        });

        document.addEventListener('click', (e) => {
            if (!input.contains(e.target) && !dropdown.contains(e.target)) {
                dropdown.classList.add('d-none');
            }
        });
    };

    return { getToken, getUser, setAuth, logout, syncShell, requireAuth, requireRole, api, formatCurrency, formatDate, toStatusClass, getLocations, buildLocationDropdown };
})();
