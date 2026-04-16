package com.hotelbooking.config;

import com.hotelbooking.model.Bill;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
import com.hotelbooking.model.Hotel;
import com.hotelbooking.model.Payment;
import com.hotelbooking.model.PaymentStatus;
import com.hotelbooking.model.Role;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.model.User;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.PaymentRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.UserRepository;
import com.hotelbooking.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final BillingService billingService;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            User admin = createUserIfMissing("admin@hotel.com", "System Admin", "9999999999", "admin123",
                    Role.ROLE_ADMIN);
            User customer = createUserIfMissing("user@hotel.com", "Rahul Sharma", "8888888888", "user123",
                    Role.ROLE_CUSTOMER);
            User customerTwo = createUserIfMissing("riya@hotel.com", "Riya Mehta", "7777777777", "user123",
                    Role.ROLE_CUSTOMER);
            User customerThree = createUserIfMissing("demo@hotel.com", "Demo Customer", "6666666666", "user123",
                    Role.ROLE_CUSTOMER);

            if (hotelRepository.count() == 0) {
                seedHotels();
            }

            if (bookingRepository.count() == 0) {
                seedBookings(admin, customer, customerTwo, customerThree);
            }
        };
    }

    private User createUserIfMissing(String email, String fullName, String phone, String password, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPhoneNumber(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            return userRepository.save(user);
        });
    }

    private void seedHotels() {
        String[][] locations = {
                { "Maharashtra", "Mumbai" }, { "Maharashtra", "Pune" }, { "Maharashtra", "Nagpur" },
                { "Maharashtra", "Nashik" }, { "Maharashtra", "Aurangabad" }, { "Maharashtra", "Mahabaleshwar" },
                { "Maharashtra", "Lonavala" },
                { "Karnataka", "Bangalore" }, { "Karnataka", "Mysore" }, { "Karnataka", "Mangalore" },
                { "Karnataka", "Hubli" }, { "Karnataka", "Coorg" }, { "Karnataka", "Gokarna" },
                { "Tamil Nadu", "Chennai" }, { "Tamil Nadu", "Coimbatore" }, { "Tamil Nadu", "Madurai" },
                { "Tamil Nadu", "Ooty" }, { "Tamil Nadu", "Kodaikanal" },
                { "Kerala", "Kochi" }, { "Kerala", "Thiruvananthapuram" }, { "Kerala", "Munnar" },
                { "Kerala", "Wayanad" }, { "Kerala", "Alleppey" }, { "Kerala", "Kozhikode" },
                { "Rajasthan", "Jaipur" }, { "Rajasthan", "Udaipur" }, { "Rajasthan", "Jodhpur" },
                { "Rajasthan", "Jaisalmer" }, { "Rajasthan", "Pushkar" }, { "Rajasthan", "Mount Abu" },
                { "Delhi", "New Delhi" }, { "Delhi", "Dwarka" }, { "Delhi", "Aerocity" },
                { "Goa", "Panaji" }, { "Goa", "Candolim" }, { "Goa", "Calangute" }, { "Goa", "Baga" },
                { "Goa", "Anjuna" }, { "Goa", "South Goa" },
                { "West Bengal", "Kolkata" }, { "West Bengal", "Darjeeling" }, { "West Bengal", "Siliguri" },
                { "West Bengal", "Digha" },
                { "Gujarat", "Ahmedabad" }, { "Gujarat", "Surat" }, { "Gujarat", "Vadodara" }, { "Gujarat", "Rajkot" },
                { "Himachal Pradesh", "Shimla" }, { "Himachal Pradesh", "Manali" },
                { "Himachal Pradesh", "Dharamshala" }, { "Himachal Pradesh", "Dalhousie" },
                { "Himachal Pradesh", "Kasauli" },
                { "Uttarakhand", "Dehradun" }, { "Uttarakhand", "Mussoorie" }, { "Uttarakhand", "Rishikesh" },
                { "Uttarakhand", "Nainital" }, { "Uttarakhand", "Haridwar" }
        };

        // Categorized Images for better distinctness
        String[] beachImages = {
                "https://i.pinimg.com/1200x/b8/e4/15/b8e4154998384f8863c731ee2d20abe1.jpg",
                "https://i.pinimg.com/1200x/8e/87/f3/8e87f361b6a14578a5d04f833217b2df.jpg",
                "https://i.pinimg.com/1200x/15/42/fa/1542faf121dde687021f3758064ec44b.jpg",
                "https://i.pinimg.com/1200x/11/d2/13/11d2139bfacbeb8f7736b850ff5281eb.jpg",
                "https://i.pinimg.com/736x/10/88/7b/10887b4ca2eaddc5969de88a71508564.jpg",
                "https://i.pinimg.com/736x/e8/5f/b0/e85fb0f61bde1ec8ad00c319c52be487.jpg",
                "https://i.pinimg.com/736x/51/06/ac/5106ac2fee002c119d36a57331f7b3fb.jpg",
                "https://i.pinimg.com/1200x/2e/1c/75/2e1c752b6a9725d0331109eb9ff576da.jpg",
                "https://i.pinimg.com/1200x/c8/6e/05/c86e05383463b4d4a5cdf38011823932.jpg",
                "https://i.pinimg.com/736x/79/78/87/7978878ad6e1bdf2f57f30fd1e23546d.jpg",
                "https://i.pinimg.com/736x/6b/7c/8f/6b7c8fe83c531dd947112f25bb7c8c48.jpg",
                "https://i.pinimg.com/736x/47/2a/dd/472add28786aa328599861a37a1371ab.jpg",
                "https://i.pinimg.com/736x/22/e7/42/22e7426e9fcd95c88d1fab3f3fbbb45d.jpg",
                "https://i.pinimg.com/736x/1f/6b/5a/1f6b5a39b7f7be2893d0db0967899f7b.jpg",
                "https://i.pinimg.com/1200x/b8/6c/2b/b86c2baf85e83fd33e3da808ab52ff1f.jpg",
                "https://i.pinimg.com/736x/92/15/01/921501f3f5cdad7aaa054fffcb221f31.jpg",
                "https://i.pinimg.com/1200x/56/31/48/5631484a6f5e51af12445902ebde9c5d.jpg",
                "https://i.pinimg.com/736x/ca/a3/44/caa344451f061880d51d6886904226ff.jpg",
                "https://i.pinimg.com/736x/67/9a/4a/679a4ae1d06b26c519410cccaf45a9f8.jpg",
                "https://i.pinimg.com/1200x/4c/77/06/4c7706c4448bc4019abde899d0f3a1de.jpg"
        };
        String[] hillImages = {
                "https://i.pinimg.com/1200x/96/20/d2/9620d29e7dae54ef3f0aeb23d1027a1f.jpg",
                "https://i.pinimg.com/736x/e9/76/39/e97639e9407e28208a801bf3c1c0de38.jpg",
                "https://i.pinimg.com/736x/57/25/96/5725966fac6da399cbaad60068cd6a8e.jpg",
                "https://i.pinimg.com/1200x/bc/5d/e7/bc5de7027c4ae8cc5d0d772e8c7ea5c1.jpg",
                "https://i.pinimg.com/1200x/da/0f/e8/da0fe8218f4988c6c27f83d58e12ab7d.jpg",
                "https://i.pinimg.com/1200x/a7/5b/50/a75b508e21ea47218a2f9cbbd089cbe1.jpg",
                "https://i.pinimg.com/1200x/16/14/72/1614723b9f6af866b8b354563ba6081c.jpg",
                "https://i.pinimg.com/736x/63/ab/1f/63ab1f0d9ea73cf557806dca9c46b2a7.jpg",
                "https://cf.bstatic.com/xdata/images/hotel/max1024x768/408899036.jpg?k=e9e2a780adec11f6b2360796a70d64a4c6f4499cdd104944590a7245f469de02&o=",
                "https://i.pinimg.com/736x/fd/1d/63/fd1d63c34fd31bc221a81ce5a2231f78.jpg",
                "https://i.pinimg.com/736x/45/36/cb/4536cb9eea5eef22997c42c2e64a6839.jpg",
                "https://i.pinimg.com/736x/8e/58/0d/8e580d177b4fbbf52f89eb698af369e8.jpg",
                "https://i.pinimg.com/736x/d7/d1/56/d7d156eae1d43fb3d0841ec1f2d1fb84.jpg",
                "https://i.pinimg.com/736x/f6/8a/34/f68a343a809ec3bd0e87d360c841b8c3.jpg",
                "https://i.pinimg.com/1200x/a2/34/1d/a2341d3a1113f2824e9bc0ca0bf275fa.jpg",
                "https://i.pinimg.com/1200x/a9/0e/7c/a90e7c44ebb747ba8fc48366dabfde48.jpg",
                "https://i.pinimg.com/736x/39/fa/80/39fa8006dd021735f97ff19c01b0906f.jpg",
                "https://i.pinimg.com/1200x/cc/68/68/cc6868c206b3cedd9ae40001b5cee7ad.jpg",
                "https://i.pinimg.com/1200x/3c/59/66/3c596604bfd0e84081acd421f5a29b15.jpg"
        };
        String[] heritageImages = {
                "https://i.pinimg.com/1200x/45/b8/d4/45b8d43b4caeaeff689f615401afa938.jpg",
                "https://i.pinimg.com/1200x/a9/ed/2a/a9ed2a19adbce0790dad8fc0fa41f405.jpg",
                "https://i.pinimg.com/1200x/04/c7/4c/04c74cca4c641e6e0096f7d687b4ff13.jpg",
                "https://i.pinimg.com/736x/0f/22/7b/0f227bb7dfce628ea0a22f6f8de756ba.jpg",
                "https://i.pinimg.com/1200x/fa/99/42/fa9942ffff3df6ab9f2616ad70b39989.jpg",
                "https://i.pinimg.com/736x/64/97/5e/64975e449965fc7e706d34e373bab09d.jpg",
                "https://i.pinimg.com/736x/9b/d6/aa/9bd6aae48ab41a4725229e59ded14277.jpg",
                "https://i.pinimg.com/736x/bf/5d/17/bf5d179d529715cb3e051dd1f63bec5d.jpg",
                "https://i.pinimg.com/1200x/e9/2f/2c/e92f2c7d8a26c5d7072791f6b8493492.jpg",
                "https://i.pinimg.com/1200x/57/ce/f3/57cef3bb9e3631a08895bf94cf134456.jpg",
                "https://i.pinimg.com/736x/c4/04/26/c404265a55ed91c20f2b370a0a294c5b.jpg",
                "https://i.pinimg.com/736x/a3/90/97/a390972c9c5706d982f8ca936e590ab0.jpg",
                "https://i.pinimg.com/1200x/16/5e/76/165e768e19819808b36670a65b95e274.jpg",
                "https://i.pinimg.com/736x/e7/9d/34/e79d3499465b34971359af141a0f123c.jpg",
                "https://i.pinimg.com/736x/de/33/5e/de335e3c71b5039e844f5c6a59d49aa5.jpg",
                "https://i.pinimg.com/1200x/e8/dd/ec/e8ddeceedafc3cd3b32a4435f389b13a.jpg",
                "https://i.pinimg.com/1200x/81/f8/f3/81f8f338ba242f0f72a94a6794b5338d.jpg",
                "https://i.pinimg.com/736x/31/43/ca/3143cab70488da4d1a54b22b3a363772.jpg"
        };
        String[] cityImages = {
                "https://i.pinimg.com/1200x/7a/a1/94/7aa19415266b8ad0ad570b7d2ecc3e8e.jpg",
                "https://i.pinimg.com/736x/0c/2f/b1/0c2fb1e4f17ce9378aa6d6b5ee30a4f4.jpg",
                "https://i.pinimg.com/736x/cd/0c/97/cd0c97eb82a713566a6c020bfaaa61ce.jpg",
                "https://i.pinimg.com/736x/48/29/f9/4829f9d07283bd5ec45e85b3ad1c621a.jpg",
                "https://i.pinimg.com/736x/0b/29/a0/0b29a0e07e21067ff229524fbdabb4d0.jpg",
                "https://i.pinimg.com/1200x/ce/9c/fc/ce9cfce6ad1dac68a39cf4f46e2fde38.jpg",
                "https://i.pinimg.com/736x/42/3b/eb/423beba0fc90c344d92c1dfb61d01156.jpg",
                "https://i.pinimg.com/736x/a8/8d/42/a88d42deaca1b9881698b0db23c6e9c3.jpg",
                "https://i.pinimg.com/736x/4c/8c/71/4c8c718943dc51dea92b1eba89bc5a94.jpg",
                "https://i.pinimg.com/736x/17/fb/7e/17fb7eb49e4ea10d78586072cbed7a49.jpg",
                "https://i.pinimg.com/736x/03/76/ba/0376baa772c722a933f7d704a036ffd4.jpg",
                "https://i.pinimg.com/736x/7b/b8/2b/7bb82be26667e6843ac37756f082d69a.jpg",
                "https://i.pinimg.com/736x/d6/ed/6e/d6ed6e5e0ed673b392fe20b8f238ff6c.jpg",
                "https://i.pinimg.com/1200x/4a/43/74/4a437412f58186ed7da517360af2b300.jpg",
                "https://i.pinimg.com/736x/da/ad/96/daad965e02aa6aab10c993b5035bde4d.jpg",
                "https://i.pinimg.com/736x/21/ed/e8/21ede80012cc0596eff8a2c455889fb4.jpg",
                "https://i.pinimg.com/736x/64/13/9b/64139ba7b90264da06b7feacc9dee986.jpg",
                "https://i.pinimg.com/1200x/47/96/66/479666cc454a39503ee4a63dc19ca58f.jpg",
                "https://i.pinimg.com/736x/c9/4c/45/c94c4532da502fed6b5dfe6a4a40debc.jpg",
                "https://i.pinimg.com/1200x/28/e8/38/28e838d5c1d4b0c219ca5108c58cdf68.jpg",
                "https://i.pinimg.com/736x/66/cd/b4/66cdb41a7f84f9da02adca74c4ffeee3.jpg"
        };

        String[] hotelImages = {
                "https://i.pinimg.com/1200x/7a/a1/94/7aa19415266b8ad0ad570b7d2ecc3e8e.jpg",
                "https://i.pinimg.com/1200x/44/ea/55/44ea551823619b4691ee2321d3175e02.jpg",
                "https://i.pinimg.com/736x/eb/85/58/eb855869aa85309946650f3ac103da5d.jpg",
                "https://i.pinimg.com/736x/de/3f/ac/de3fac6ebe5755591275d2d95c6c71ee.jpg",
                "https://i.pinimg.com/736x/42/3b/eb/423beba0fc90c344d92c1dfb61d01156.jpg",
                "https://i.pinimg.com/736x/32/be/02/32be02fec19e98d3c5eb7a8e09a1d1fa.jpg",
                "https://i.pinimg.com/1200x/ee/85/7d/ee857dd35064a19fb564e7656f64c1ac.jpg",
                "https://i.pinimg.com/736x/1c/19/3e/1c193eeaa091b88a63c5bce7cf89cd6c.jpg",
                "https://i.pinimg.com/736x/a4/b8/95/a4b895781aa31c0588ee6f42c90859e2.jpg",
                "https://i.pinimg.com/1200x/99/70/b2/9970b2028d7ab62edef8b98578248532.jpg",
                "https://i.pinimg.com/736x/7d/0d/13/7d0d13c551230ab059959dbe736a8152.jpg",
                "https://i.pinimg.com/736x/16/32/0c/16320cde6b6a7528c646023ad8a97732.jpg",
                "https://i.pinimg.com/1200x/9b/ac/62/9bac6291b8a5c8d02af49d6dbc2a058c.jpg",
                "https://i.pinimg.com/736x/a4/12/9f/a4129f56eb5543471515a6cc157e20d2.jpg",
                "https://i.pinimg.com/736x/49/fb/f0/49fbf0c1463bc4b1a2260b64e6f0519d.jpg",
                "https://i.pinimg.com/736x/dd/74/07/dd74077947c67161685bb0e6632445dc.jpg",
                "https://i.pinimg.com/736x/64/18/2d/64182db0aa42663ff914774f11da8c16.jpg",
                "https://i.pinimg.com/1200x/0a/68/fc/0a68fcc32f9c7405b431479fa3791ffe.jpg",
                "https://i.pinimg.com/1200x/52/6c/33/526c33bb7b0d4b4f5739e80f7afb545b.jpg",
                "https://i.pinimg.com/1200x/1c/83/00/1c83001e7e748f25c18ba5a972833163.jpg",
                "https://i.pinimg.com/736x/b8/d6/f6/b8d6f69abab374948abd73d146b00c50.jpg",
                "https://i.pinimg.com/1200x/2b/07/4d/2b074d520e3149ceb2559b3a85f03d42.jpg",
                "https://i.pinimg.com/1200x/be/27/da/be27da34ae75bbbf6bb0a0c7565b2bdd.jpg",
                "https://i.pinimg.com/736x/bf/4a/35/bf4a35f8c2bcf5f58190b6183b4bd14c.jpg",
                "https://i.pinimg.com/736x/85/61/8f/85618fc0dee3e6e5eaf85060dd01f33c.jpg",
                "https://i.pinimg.com/1200x/17/d2/71/17d271a87c655aa1018ed811296ff771.jpg",
                "https://i.pinimg.com/736x/f8/53/02/f853028d97842ccb31212819b1208b46.jpg",
                "https://i.pinimg.com/736x/9b/61/b8/9b61b838710bccf893ef5845c68e84bc.jpg",
                "https://i.pinimg.com/736x/3c/1c/8f/3c1c8f8fc8fa5badf053bfdbde87a5d5.jpg"
        };
        String[] roomImages = {
                "https://i.pinimg.com/1200x/ad/6b/a7/ad6ba7bf5446d0acbc39adb41cbc94c9.jpg",
                "https://i.pinimg.com/1200x/54/7c/b8/547cb88dbd2a5df54a8a3d207656934a.jpg",
                "https://i.pinimg.com/736x/68/24/82/682482fd4126160027a439fbad15a622.jpg",
                "https://i.pinimg.com/736x/79/ba/46/79ba463c09cae3f2543c6b237a11f8d8.jpg",
                "https://i.pinimg.com/1200x/13/37/79/1337792a5c9ecb822c3cb3ee57e41ce7.jpg",
                "https://i.pinimg.com/1200x/e0/d4/e5/e0d4e5a6d010531c2d89ecb5c55e0305.jpg",
                "https://i.pinimg.com/736x/08/3f/f1/083ff1caf4ac004aea6e04bc2da255f0.jpg",
                "https://i.pinimg.com/736x/c4/1a/c7/c41ac7200b6377d7d5f96330783ee1b1.jpg",
                "https://i.pinimg.com/736x/3b/d2/5e/3bd25e8963584a13f000a53e93e53b03.jpg",
                "https://i.pinimg.com/736x/7a/bb/6d/7abb6d0fc7eb60081891292b77aa4b18.jpg",
                "https://i.pinimg.com/1200x/71/41/8c/71418cd81c3ff8e8755f726a551464e0.jpg",
                "https://i.pinimg.com/1200x/72/c9/21/72c921591058bba1215c367dc0d91708.jpg",
                "https://i.pinimg.com/736x/de/fd/c9/defdc9bb2fa55c87a991aee661a8a60c.jpg",
                "https://i.pinimg.com/1200x/5c/69/e8/5c69e81a6cd615c5cf8a0bf17596abec.jpg",
                "https://i.pinimg.com/736x/22/18/4d/22184d22b4ea5819d81c01a448154d7b.jpg",
                "https://i.pinimg.com/1200x/13/a8/b3/13a8b3f6ccdec0100892c7e1835c5976.jpg",
                "https://i.pinimg.com/736x/38/a4/37/38a4376e1a59c86bc4b3a81044dd384f.jpg",
                "https://i.pinimg.com/736x/94/b1/a9/94b1a978a3712023c042124d786807bc.jpg",
                "https://i.pinimg.com/736x/9f/92/ef/9f92efd82ff8216e5b65c2b29d46d1d6.jpg"
        };
        String[] washroomImages = {
                "https://i.pinimg.com/1200x/f6/66/85/f66685db9c19661176c4db3a5f357033.jpg",
                "https://i.pinimg.com/736x/80/0f/44/800f44697b2266c5f727726ffb5a1c2b.jpg",
                "https://i.pinimg.com/736x/ea/33/0c/ea330c91cf82e26401c1c89557492960.jpg",
                "https://i.pinimg.com/1200x/b7/52/ca/b752ca86860c55b936d160f8ac4490d0.jpg",
                "https://i.pinimg.com/736x/ba/45/4c/ba454cc58488c9fafe7e6b73d359fa98.jpg",
                "https://i.pinimg.com/736x/77/1d/78/771d78ce1033b458c81259eb5184aed5.jpg",
                "https://i.pinimg.com/736x/d6/6e/84/d66e8474fddf24ae99c17823c880e35e.jpg",
                "https://i.pinimg.com/originals/b6/1b/62/b61b6200c865912db784ea35cbd1bbc8.png",
                "https://i.pinimg.com/736x/8b/ca/1f/8bca1f684769a587cf23483c345d2772.jpg",
                "https://i.pinimg.com/1200x/33/e7/28/33e7280d2e30cddf56852aef01881894.jpg",
                "https://i.pinimg.com/736x/4d/d4/42/4dd4428ddf281ac0f52a4b081aa0a1f1.jpg",
                "https://i.pinimg.com/1200x/40/28/8f/40288fd8a66b6d4c5b32dc32c32dde7d.jpg"
        };
        String[] balconyImages = {
                "https://i.pinimg.com/1200x/a1/ac/06/a1ac0698109b88f4bf60473a35d56f3f.jpg",
                "https://i.pinimg.com/736x/00/a8/e3/00a8e3241c1473b9366fd5e3ac4f2556.jpg",
                "https://i.pinimg.com/1200x/84/4f/c4/844fc41cf52ad5a273baba2febf79e79.jpg",
                "https://i.pinimg.com/1200x/9e/7b/c4/9e7bc4296cb76b04a238385ef8fec15d.jpg",
                "https://i.pinimg.com/736x/c1/2c/79/c12c7970491d4d3db92ebe22ee8a35d8.jpg",
                "https://i.pinimg.com/736x/bb/2a/76/bb2a76f4a406e9f2fd04aa7756c38e49.jpg",
                "https://i.pinimg.com/736x/77/8b/4d/778b4d04769d90da951a896936cb3f11.jpg",
                "https://i.pinimg.com/736x/d2/36/b6/d236b62ff555147c79ca7cda8d01403c.jpg",
                "https://i.pinimg.com/736x/9f/46/23/9f4623c32d2fa11c101e928be69a49a6.jpg",
                "https://i.pinimg.com/736x/b1/df/a7/b1dfa7b1c00fef479740b53b2d68aabb.jpg",
                "https://i.pinimg.com/1200x/a5/27/a7/a527a7b76513d07a2c95eefda50c2876.jpg",
                "https://i.pinimg.com/736x/81/d1/c7/81d1c7c62679983f203eb93d083bb960.jpg"
        };

        String[] hotelPrefixes = { "The Grand", "Royal", "Azure", "Crystal", "Golden", "Majestic", "Serene", "Harbor",
                "Mountain", "River", "Sunset", "Emerald", "Platinum", "Regent", "Oasis" };
        String[] hotelSuffixes = { "Retreat", "Palace", "Resort", "Inn", "Suites", "Lodge", "Boutique", "Plaza",
                "Heights", "Court", "Manor", "Villa" };

        String[] baseAmenities = { "Free Breakfast", "Buffet", "AC", "Bathtub", "Pool", "WiFi", "Gym", "Spa", "Parking",
                "Bar", "Room Service", "Airport Shuttle" };
        java.util.Random rand = new java.util.Random();
        int beachIdx = 0, hillIdx = 0, heritageIdx = 0, cityIdx = 0;
        int roomIdx = 0, washroomIdx = 0, balconyIdx = 0;

        int hotelCounter = 1;
        for (String[] loc : locations) {
            String state = loc[0];
            String city = loc[1];

            int numHotels = 2 + rand.nextInt(2); // 2 to 3 for variety but not too many
            for (int h = 0; h < numHotels; h++) {
                String name = hotelPrefixes[rand.nextInt(hotelPrefixes.length)] + " "
                        + hotelSuffixes[rand.nextInt(hotelSuffixes.length)];

                int amtCount = 4 + rand.nextInt(4);
                java.util.List<String> amList = new java.util.ArrayList<>();
                if (rand.nextBoolean())
                    amList.add("Free Breakfast");
                if (rand.nextBoolean())
                    amList.add("Buffet");
                if (rand.nextBoolean())
                    amList.add("AC");
                if (rand.nextBoolean())
                    amList.add("Bathtub");

                while (amList.size() < amtCount) {
                    String am = baseAmenities[rand.nextInt(baseAmenities.length)];
                    if (!amList.contains(am))
                        amList.add(am);
                }
                String amenities = String.join(", ", amList);

                double rating = 3.5 + (rand.nextDouble() * 1.5);
                rating = Math.round(rating * 10.0) / 10.0;

                String mainImage;
                String lowerCity = city.toLowerCase();
                if (lowerCity.contains("goa") || lowerCity.contains("kochi") || lowerCity.contains("chennai") ||
                        lowerCity.contains("mangalore") || lowerCity.contains("gokarna")
                        || lowerCity.contains("alleppey")) {
                    mainImage = beachImages[beachIdx++ % beachImages.length];
                } else if (lowerCity.contains("manali") || lowerCity.contains("shimla") || lowerCity.contains("ooty") ||
                        lowerCity.contains("munnar") || lowerCity.contains("coorg") || lowerCity.contains("mussoorie")
                        ||
                        lowerCity.contains("rishikesh") || lowerCity.contains("darjeeling")) {
                    mainImage = hillImages[hillIdx++ % hillImages.length];
                } else if (lowerCity.contains("jaipur") || lowerCity.contains("udaipur")
                        || lowerCity.contains("jodhpur") ||
                        lowerCity.contains("jaisalmer") || lowerCity.contains("mysore")
                        || lowerCity.contains("aurangabad")) {
                    mainImage = heritageImages[heritageIdx++ % heritageImages.length];
                } else {
                    mainImage = cityImages[cityIdx++ % cityImages.length];
                }

                String roomImg = roomImages[roomIdx++ % roomImages.length];
                String washroomImg = washroomImages[washroomIdx++ % washroomImages.length];
                String balconyImg = balconyImages[balconyIdx++ % balconyImages.length];

                Hotel hotel = saveHotel(name, city, "Central " + city + ", " + state,
                        "A wonderful stay experience in the heart of " + city + ".",
                        amenities, mainImage, roomImg, washroomImg, balconyImg, rating);

                int roomsToGen = 2 + rand.nextInt(3);
                for (int i = 1; i <= roomsToGen; i++) {
                    RoomType type = RoomType.values()[rand.nextInt(RoomType.values().length)];
                    int price = 2000 + (rand.nextInt(8) * 1000);
                    if (type == RoomType.SUITE)
                        price += 5000;
                    int cap = (type == RoomType.SINGLE) ? 1 : (type == RoomType.SUITE ? 4 : 2);
                    saveRoom(hotel, city.substring(0, Math.min(3, city.length())).toUpperCase() + "-"
                            + (100 * hotelCounter + i), type, price, cap, true);
                }
                hotelCounter++;
            }
        }
    }

    private Hotel saveHotel(String name, String location, String address, String description, String amenities,
            String imageUrl, String roomImg, String washroomImg, String balconyImg, double rating) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setAddress(address);
        hotel.setDescription(description);
        hotel.setAmenities(amenities);
        hotel.setImageUrl(imageUrl);
        hotel.setRoomImageUrl(roomImg);
        hotel.setWashroomImageUrl(washroomImg);
        hotel.setBalconyImageUrl(balconyImg);
        hotel.setRating(rating);
        hotel.setTotalRooms(5);
        hotel.setAvailableRooms(5);
        return hotelRepository.save(hotel);
    }

    private Room saveRoom(Hotel hotel, String roomNumber, RoomType roomType, int price, int capacity,
            boolean available) {
        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setPricePerNight(BigDecimal.valueOf(price));
        room.setCapacity(capacity);
        room.setAvailable(available);
        return roomRepository.save(room);
    }

    private void seedBookings(User admin, User customer, User customerTwo, User customerThree) {
        List<Room> rooms = roomRepository.findAll();
        Booking confirmedBooking = createBooking(customer, rooms.get(0), LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(8), 2, PaymentStatus.SUCCESS, BookingStatus.CONFIRMED, true);
        createBooking(customerTwo, rooms.get(3), LocalDate.now().plusDays(9), LocalDate.now().plusDays(12), 2,
                PaymentStatus.PENDING, BookingStatus.PENDING, false);
        createBooking(customerThree, rooms.get(6), LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), 2,
                PaymentStatus.SUCCESS, BookingStatus.CONFIRMED, true);
        createBooking(admin, rooms.get(9), LocalDate.now().plusDays(15), LocalDate.now().plusDays(17), 1,
                PaymentStatus.FAILED, BookingStatus.PENDING, false);

        Bill bill = billingService.generateOrUpdateBill(confirmedBooking);
        Payment payment = paymentRepository.findByBookingId(confirmedBooking.getId()).orElseGet(Payment::new);
        payment.setBooking(confirmedBooking);
        payment.setAmount(bill.getFinalAmount());
        payment.setPaymentMethod("CARD");
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN-DEMO-0001");
        payment.setRemarks("Demo payment seeded for showcase");
        paymentRepository.save(payment);
    }

    private Booking createBooking(User user, Room room, LocalDate checkIn, LocalDate checkOut, int guests,
            PaymentStatus paymentStatus, BookingStatus bookingStatus, boolean generateBill) {
        if (!bookingRepository.findOverlappingBookings(room.getId(), checkIn, checkOut,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN)).isEmpty()) {
            // Overlapping check disabled as requested by user to allow multiple bookings
            // for testing
            // throw new BadRequestException("Room is not available for the selected
            // dates");
        }
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setHotel(room.getHotel());
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfGuests(guests);
        booking.setTotalNights((int) (checkOut.toEpochDay() - checkIn.toEpochDay()));
        booking.setTotalPrice(room.getPricePerNight().multiply(BigDecimal.valueOf(booking.getTotalNights())));
        booking.setPaymentStatus(paymentStatus);
        booking.setBookingStatus(bookingStatus);
        Booking saved = bookingRepository.save(booking);
        if (generateBill) {
            billingService.generateOrUpdateBill(saved);
        }
        return saved;
    }
}
