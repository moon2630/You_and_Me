package com.example.uptrendseller;

import java.util.HashMap;
import java.util.Map;

public class CommissionCalculator {

    // Category and Subcategory commission rates
    private static final Map<String, Map<String, Integer>> COMMISSION_MAP = new HashMap<>();

    private static final HashMap<String, Integer> CATEGORY_COMMISSION = new HashMap<>();



    static {

            // ============================
            // ART
            // ============================
            Map<String, Integer> art = new HashMap<>();
            art.put("Paintings", 22);
            art.put("Digital Art", 20);
            art.put("Drawings", 18);
            art.put("Sculptures", 25);
            art.put("Prints", 15);
            art.put("Photography", 18);
            art.put("Art Supplies", 12);
            art.put("Ceramics", 20);
            art.put("Textile Art", 22);
            art.put("Mixed Media", 24);
            COMMISSION_MAP.put("Art", art);

            // ============================
            // BEAUTY
            // ============================
            Map<String, Integer> beauty = new HashMap<>();
            beauty.put("Skincare", 15);
            beauty.put("Makeup", 18);
            beauty.put("Haircare", 12);
            beauty.put("Nail Care", 14);
            beauty.put("Fragrances", 16);
            beauty.put("Bath and Body", 14);
            beauty.put("Men's Grooming", 12);
            beauty.put("Wellness and Self-Care", 10);
            beauty.put("Sun Care", 12);
            beauty.put("Eye Care", 12);
            beauty.put("Natural and Organic Beauty", 16);
            beauty.put("Anti-Aging Products", 18);
            beauty.put("Travel-Size Beauty", 10);
            beauty.put("Foot Care", 12);
            COMMISSION_MAP.put("Beauty", beauty);

            // ============================
            // BABY PRODUCTS
            // ============================
            Map<String, Integer> baby = new HashMap<>();
            baby.put("Diapers and Changing Supplies", 10);
            baby.put("Feeding Essentials", 12);
            baby.put("Baby Clothing", 14);
            baby.put("Nursery Furniture", 15);
            baby.put("Strollers and Car Seats", 18);
            baby.put("Baby Safety Products", 15);
            baby.put("Baby Toys", 12);
            baby.put("Health and Grooming", 12);
            baby.put("Baby Gear and Accessories", 10);
            baby.put("Bedding and Blankets", 12);
            COMMISSION_MAP.put("Baby Products", baby);

            // ============================
            // BOOKS
            // ============================
            Map<String, Integer> books = new HashMap<>();
            books.put("Fiction", 12);
            books.put("Non-Fiction", 12);
            books.put("Mystery and Thriller", 12);
            books.put("Science Fiction and Fantasy", 14);
            books.put("Romance", 12);
            books.put("Biographies and Memoirs", 12);
            books.put("Self-Help and Personal Development", 10);
            books.put("History and Politics", 10);
            books.put("Cookbooks and Food", 12);
            books.put("Children's Books", 12);
            books.put("Horror", 12);
            books.put("Magical Realism", 14);
            books.put("Adventure", 12);
            COMMISSION_MAP.put("Books", books);

            // ============================
            // CAMERAS
            // ============================
            Map<String, Integer> cameras = new HashMap<>();
            cameras.put("DSLR Cameras", 10);
            cameras.put("Mirrorless Cameras", 10);
            cameras.put("Point-and-Shoot Cameras", 12);
            cameras.put("Action Cameras", 12);
            cameras.put("Film Cameras", 15);
            cameras.put("360-Degree Cameras", 12);
            cameras.put("Instant Cameras", 15);
            cameras.put("Bridge Cameras", 12);
            cameras.put("Medium Format Cameras", 10);
            cameras.put("Camera Accessories", 12);
            COMMISSION_MAP.put("Cameras", cameras);

            // ============================
            // CELL PHONES & ACCESSORIES
            // ============================
            Map<String, Integer> mobiles = new HashMap<>();
            mobiles.put("Smartphones", 8);
            mobiles.put("Phone Cases and Covers", 12);
            mobiles.put("Screen Protectors", 12);
            mobiles.put("Chargers and Cables", 10);
            mobiles.put("Power Banks", 10);
            mobiles.put("Bluetooth Headsets", 12);
            mobiles.put("Phone Mounts and Holders", 12);
            mobiles.put("Wireless Earbuds", 14);
            mobiles.put("PopSockets and Phone Grips", 10);
            mobiles.put("Cell Phone Accessories Kits", 12);
            COMMISSION_MAP.put("Cell Phones & Accessories", mobiles);

            // ============================
            // CHOCOLATES
            // ============================
            Map<String, Integer> chocolate = new HashMap<>();
            chocolate.put("Dark Chocolate", 10);
            chocolate.put("Milk Chocolate", 10);
            chocolate.put("White Chocolate", 10);
            chocolate.put("Truffles", 12);
            chocolate.put("Chocolate Bars", 10);
            chocolate.put("Chocolate Covered Nuts", 12);
            chocolate.put("Chocolate Covered Fruits", 12);
            chocolate.put("Assorted Chocolates", 12);
            chocolate.put("Sugar-Free Chocolate", 10);
            chocolate.put("Artisanal Chocolate", 14);
            COMMISSION_MAP.put("Chocolates", chocolate);

            // ============================
            // ELECTRONICS
            // ============================
            Map<String, Integer> electronics = new HashMap<>();
            electronics.put("Televisions", 10);
            electronics.put("Home Theater Systems", 12);
            electronics.put("Audio Speakers", 12);
            electronics.put("Headphones and Earphones", 12);
            electronics.put("Fitness Trackers", 12);
            electronics.put("Camcorders", 10);
            electronics.put("Projectors", 10);
            electronics.put("VR Headsets", 12);
            electronics.put("Drones", 12);
            electronics.put("Digital Photo Frames", 12);
            electronics.put("Home Security Systems", 10);
            electronics.put("Smart Home Devices", 12);
            electronics.put("Electronic Gadgets and Gizmos", 10);
            COMMISSION_MAP.put("Electronics", electronics);

            // ============================
            // EYEWEAR
            // ============================
            Map<String, Integer> eyewear = new HashMap<>();
            eyewear.put("Sunglasses", 14);
            eyewear.put("Prescription Glasses", 12);
            eyewear.put("Reading Glasses", 12);
            eyewear.put("Blue Light Blocking Glasses", 12);
            eyewear.put("Sports Glasses", 14);
            eyewear.put("Safety Glasses", 10);
            eyewear.put("Fashion Glasses", 12);
            eyewear.put("Clip-On Sunglasses", 12);
            eyewear.put("Kids' Eyewear", 12);
            eyewear.put("Goggles", 12);
            COMMISSION_MAP.put("Eyewear", eyewear);

            // ============================
            // FOOTWEAR
            // ============================
            Map<String, Integer> footwear = new HashMap<>();
            footwear.put("High Heels", 15);
            footwear.put("Flats and Ballerinas", 12);
            footwear.put("Sandals", 12);
            footwear.put("Sneakers", 14);
            footwear.put("Boots", 16);
            footwear.put("Wedges", 14);
            footwear.put("Espadrilles", 12);
            footwear.put("Loafers", 12);
            footwear.put("Oxfords", 14);
            footwear.put("Slippers", 10);
            footwear.put("Flip-Flops", 10);
            footwear.put("Platform Shoes", 14);
            footwear.put("Mules", 12);
            footwear.put("Athletic Shoes", 14);
            footwear.put("Outdoor and Hiking Shoes", 16);
            COMMISSION_MAP.put("Footwear", footwear);

            // ============================
            // FOOD & GROCERY
            // ============================
            Map<String, Integer> grocery = new HashMap<>();
            grocery.put("Snacks", 10);
            grocery.put("Beverages", 10);
            grocery.put("Sauces", 10);
            grocery.put("Baking Supplies", 8);
            grocery.put("Breakfast Foods", 10);
            grocery.put("Packaged Foods", 10);
            grocery.put("Pasta & Noodles", 10);
            grocery.put("Rice", 8);
            grocery.put("Cooking Oils", 8);
            grocery.put("Spices & Seasonings", 10);
            COMMISSION_MAP.put("Food & Grocery", grocery);

            // ============================
            // HEALTH & PERSONAL CARE
            // ============================
            Map<String, Integer> health = new HashMap<>();
            health.put("Skincare", 12);
            health.put("Hair Care", 10);
            health.put("Oral Care", 10);
            health.put("Personal Hygiene", 10);
            health.put("Feminine Care", 12);
            health.put("Sexual Wellness", 12);
            health.put("First Aid", 10);
            health.put("Diet & Nutrition", 12);
            COMMISSION_MAP.put("Health & Personal Care", health);

            // ============================
            // HOME & LIVING
            // ============================
            Map<String, Integer> home = new HashMap<>();
            home.put("Furniture", 15);
            home.put("Home Décor", 14);
            home.put("Kitchen & Dining", 12);
            home.put("Outdoor Living", 14);
            home.put("Home Improvement", 12);
            home.put("Cleaning Supplies", 10);
            home.put("Home Appliances", 12);
            COMMISSION_MAP.put("Home & Living", home);

            // ============================
            // JEWELRY
            // ============================
            Map<String, Integer> jewelry = new HashMap<>();
            jewelry.put("Rings", 18);
            jewelry.put("Necklaces", 18);
            jewelry.put("Earrings", 18);
            jewelry.put("Bracelets", 18);
            jewelry.put("Pendants", 18);
            jewelry.put("Brooches", 16);
            jewelry.put("Anklets", 16);
            jewelry.put("Charms", 14);
            jewelry.put("Chains", 18);
            COMMISSION_MAP.put("Jewelry", jewelry);

            // ============================
            // MEN'S BOTTOMWEAR
            // ============================
            Map<String, Integer> mensBottom = new HashMap<>();
            mensBottom.put("Jeans", 12);
            mensBottom.put("Trousers and Chinos", 12);
            mensBottom.put("Shorts", 12);
            mensBottom.put("Activewear", 10);
            mensBottom.put("Underwear", 10);
            mensBottom.put("Sleepwear", 10);
            COMMISSION_MAP.put("Men's Bottomwear", mensBottom);

            // ============================
            // MEN'S TOPWEAR
            // ============================
            Map<String, Integer> mensTop = new HashMap<>();
            mensTop.put("T-Shirts", 12);
            mensTop.put("Shirts", 10);
            mensTop.put("Polo Shirts", 12);
            mensTop.put("Jackets and Coats", 10);
            mensTop.put("Sweaters and Hoodies", 12);
            mensTop.put("Suits and Blazers", 14);
            mensTop.put("Swimwear", 10);
            COMMISSION_MAP.put("Men's Topwear", mensTop);

            // ============================
            // WOMEN'S BOTTOMWEAR
            // ============================
            Map<String, Integer> womensBottom = new HashMap<>();
            womensBottom.put("Jeans", 12);
            womensBottom.put("Skirts", 12);
            womensBottom.put("Pants and Trousers", 12);
            womensBottom.put("Shorts", 12);
            womensBottom.put("Activewear", 12);
            womensBottom.put("Lingerie and Intimates", 14);
            womensBottom.put("Sleepwear", 10);
            COMMISSION_MAP.put("Women's Bottomwear", womensBottom);

            // ============================
            // WOMEN'S TOPWEAR
            // ============================
            Map<String, Integer> womensTop = new HashMap<>();
            womensTop.put("Dresses", 14);
            womensTop.put("Tops and Blouses", 12);
            womensTop.put("T-Shirts and Tank Tops", 12);
            womensTop.put("Saree", 16);
            womensTop.put("Jackets and Coats", 12);
            womensTop.put("Sweaters and Hoodies", 12);
            womensTop.put("Swimwear", 12);
            womensTop.put("Maternity Clothing", 12);
            COMMISSION_MAP.put("Women's Topwear", womensTop);

            // ============================
            // TOYS
            // ============================
            Map<String, Integer> toys = new HashMap<>();
            toys.put("Teddy Bear", 12);
            toys.put("Dolls", 12);
            toys.put("Board Games", 12);
            toys.put("Building Blocks", 12);
            toys.put("Puzzles", 12);
            toys.put("Remote-Controlled Toys", 14);
            toys.put("Educational Toys", 14);
            toys.put("Outdoor Toys", 12);
            toys.put("Arts and Crafts", 10);
            COMMISSION_MAP.put("Toys", toys);

            // ============================
            // SPORTS
            // ============================
            Map<String, Integer> sports = new HashMap<>();
            sports.put("Soccer/Football", 12);
            sports.put("Basketball", 12);
            sports.put("Tennis", 12);
            sports.put("Swimming", 10);
            sports.put("Volleyball", 12);
            sports.put("Golf", 14);
            sports.put("Cricket", 12);
            sports.put("Cycling", 12);
            COMMISSION_MAP.put("Sports", sports);

            // ============================
            // COMPUTERS
            // ============================
            Map<String, Integer> computers = new HashMap<>();
            computers.put("Laptops", 10);
            computers.put("Desktops", 10);
            computers.put("Gaming PCs", 10);
            computers.put("Mini PCs", 10);
            computers.put("Chromebooks", 10);
            computers.put("Ultrabooks", 10);
            COMMISSION_MAP.put("Computers", computers);

            // ============================
            // WATCHES
            // ============================
            Map<String, Integer> watches = new HashMap<>();
            watches.put("Diving Watches", 16);
            watches.put("Pilot Watches", 18);
            watches.put("Chronograph Watches", 18);
            watches.put("Dress Watches", 16);
            watches.put("Sports Watches", 14);
            watches.put("Smartwatches", 12);
            watches.put("Mechanical Watches", 18);
            watches.put("Automatic Watches", 18);
            watches.put("Luxury Watches", 22);
            watches.put("Vintage Watches", 20);
            COMMISSION_MAP.put("Watches", watches);

            // ============================
            // OFFICE & STATIONERY
            // ============================
            Map<String, Integer> office = new HashMap<>();
            office.put("Pens and Writing Instruments", 10);
            office.put("Paper Products", 10);
            office.put("Presentation Supplies", 12);
            office.put("Desk Accessories", 10);
            office.put("Office Electronics", 10);
            office.put("Stationery", 10);
            office.put("Calendars and Planners", 10);
            COMMISSION_MAP.put("Office & Stationery", office);

        }

    /**
     * Get commission percentage for a given category and subcategory
     */
    public static int getCommissionPercentage(String category, String subCategory) {
        if (category == null || subCategory == null) {
            return 10; // Default 10%
        }

        Map<String, Integer> categoryCommissions = COMMISSION_MAP.get(category);
        if (categoryCommissions != null) {
            Integer commission = categoryCommissions.get(subCategory);
            if (commission != null) {
                return commission;
            }
        }

        // Return default commission if not found
        return 10;
    }

    /**
     * Calculate commission amount
     */
    public static double calculateCommission(double sellingPrice, String category, String subCategory) {
        int commissionPercent = getCommissionPercentage(category, subCategory);
        return (sellingPrice * commissionPercent) / 100;
    }

    /**
     * Calculate final payout to seller
     */
    public static double calculatePayout(double sellingPrice, String category, String subCategory) {
        double commission = calculateCommission(sellingPrice, category, subCategory);
        double platformFee = 30.0; // Fixed platform fee
        return sellingPrice - commission - platformFee;
    }

    public static double calculateCommission(double sellingPrice, String category) {
        int percentage = getCommissionPercentage(category);
        return (sellingPrice * percentage) / 100;
    }

    public static int getCommissionPercentage(String category) {
        if (category != null && CATEGORY_COMMISSION.containsKey(category)) {
            return CATEGORY_COMMISSION.get(category);
        }
        return 10; // Default 10%
    }

    public static double calculatePlatformFee(double sellingPrice) {
        return 30.0; // Fixed platform fee
    }

    public static double calculateNetAmount(double sellingPrice, double commissionAmount, double platformFee) {
        return sellingPrice - commissionAmount - platformFee;
    }
}




