package com.example.uptrend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapteranddatamodel.DiscountRange;
import com.example.adapteranddatamodel.PriceRange;
import com.example.uptrend.Adapter.Onclick;
import com.example.uptrend.Adapter.RecentSearchAdapter;
import com.example.uptrend.Adapter.SearchProductAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import DataModel.Product;
import io.github.muddz.styleabletoast.StyleableToast;

public class search_product extends AppCompatActivity implements Onclick {
    private TextView iv_mic, txtFilter;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private ArrayList<Product> searchProductArrayList;
    private ArrayList<Product> originalProductArrayList;
    private SearchProductAdapter searchProductAdapter;
    private RecyclerView recyclerViewSearchProduct;
    private EditText searchView;
    private CardView filter_CardView;
    private String searchQuery = "";
    private ArrayList<String> colour;
    private String activityName;
    private ArrayList<PriceRange> price;
    private String gender;
    private ArrayList<DiscountRange> discount;
    private String brand;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Handler toastHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private StyleableToast currentToast;
    private static final long SEARCH_DELAY = 300;
    private static final long TOAST_DURATION = 2000;
    private SharedPreferences sharedPreferences;
    private static final String RECENT_SEARCHES_KEY = "recent_searches";
    private ArrayList<String> recentSearches = new ArrayList<>();
    private RecyclerView recyclerViewRecentSearch;
    private RecentSearchAdapter recentSearchAdapter;
    private LinearLayout recentSearchContainer;
    private LinearLayoutManager recentLayoutManager;


    private static final String STATE_QUERY = "searchQuery";
    private static final String STATE_RESULTS = "searchResults";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenyouuu));
        }
        // Initialize views
        searchView = findViewById(R.id.searchView);
        txtFilter = findViewById(R.id.txtFilter);
        iv_mic = findViewById(R.id.iv_mic);
        recyclerViewSearchProduct = findViewById(R.id.recyclerViewSearchProduct);
        txtFilter.setVisibility(View.GONE);

        // Initialize data structures
        searchProductArrayList = new ArrayList<>();
        originalProductArrayList = new ArrayList<>();
        colour = new ArrayList<>();
        price = new ArrayList<>();
        discount = new ArrayList<>();

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchProductAdapter = new SearchProductAdapter(this, searchProductArrayList, this);
        recyclerViewSearchProduct.setLayoutManager(layoutManager);
        recyclerViewSearchProduct.setAdapter(searchProductAdapter);

        // Get intent data
        activityName = getIntent().getStringExtra("activityName");
        searchQuery = getIntent().getStringExtra("searchQuery");
        colour = getIntent().getStringArrayListExtra("colour");
        gender = getIntent().getStringExtra("gender");
        brand = getIntent().getStringExtra("brand");
        price = (ArrayList<PriceRange>) getIntent().getSerializableExtra("price");
        discount = (ArrayList<DiscountRange>) getIntent().getSerializableExtra("discount");

        // Focus search bar and show keyboard
        searchView.setFocusableInTouchMode(true);
        searchView.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        recentSearchContainer = findViewById(R.id.recentSearchContainer); // Add this ID in XML
        recyclerViewRecentSearch = findViewById(R.id.recyclerViewRecentSearch); // Add this ID in XML
        sharedPreferences = getSharedPreferences("SearchPrefs", MODE_PRIVATE);
        recentSearches = getRecentSearches();

        recentSearchAdapter = new RecentSearchAdapter(this, recentSearches,
                new RecentSearchAdapter.OnRecentSearchClickListener() {
                    @Override
                    public void onRecentSearchClick(String searchText) {
                        onRecentSearchClicked(searchText);
                    }

                    @Override
                    public void onRecentSearchRemove(String searchText, int position) {
                        removeRecentSearch(searchText, position);
                    }
                });


        recentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewRecentSearch.setLayoutManager(recentLayoutManager);
        recyclerViewRecentSearch.setAdapter(recentSearchAdapter);

        updateRecentSearchVisibility();


        if (("filterActivity".equals(activityName) || "filterActivityNoApply".equals(activityName)
                || "openProduct".equals(activityName) || "openCategoryProduct".equals(activityName)
                || "openBrandProduct".equals(activityName)) && searchQuery != null && !searchQuery.isEmpty()) {
            searchView.setText(searchQuery);
            searchProduct(searchQuery, "filterActivity".equals(activityName));
            searchView.clearFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else if ("notification".equals(activityName) || "ratingProducts".equals(activityName)
                || "home".equals(activityName) || "accountUser".equals(activityName)
                || "wishlistProduct".equals(activityName)) {
            searchView.setText("");
            searchView.clearFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            searchView.setFocusableInTouchMode(true);
            searchView.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        iv_mic.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search");
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                showToast("Error: " + e.getMessage());
            }
        });
        // Filter click listener
        txtFilter.setOnClickListener(v -> {
            Intent intent = new Intent(this, filter_product.class);
            intent.putExtra("searchQuery", searchQuery);
            startActivity(intent);
            finish();
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();

                // Update recent search visibility
                updateRecentSearchVisibility();

                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    if (searchQuery.isEmpty()) {
                        searchProductArrayList.clear();
                        originalProductArrayList.clear();
                        searchProductAdapter.notifyDataSetChanged();
                        updateRecentSearchVisibility();
                    } else {
                        // DON'T save to recent searches here - only search
                        searchProduct(searchQuery, false);
                        // Hide recent searches when typing
                        recentSearchContainer.setVisibility(View.GONE);
                        recyclerViewSearchProduct.setVisibility(View.VISIBLE);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // Set search button listener on keyboard
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery = searchView.getText().toString().trim();
                if (!searchQuery.isEmpty()) {
                    // Save to recent searches ONLY when search button pressed
                    saveRecentSearch(searchQuery);
                    searchProduct(searchQuery, false);
                    hideKeyboard();
                }
                return true;
            }
            return false;
        });
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }




    private void removeRecentSearch(String searchText, int position) {
        if (position >= 0 && position < recentSearches.size()) {
            // Remove from list
            recentSearches.remove(position);

            // Update SharedPreferences
            saveAllRecentSearches();

            // Update adapter
            recentSearchAdapter.notifyItemRemoved(position);
            recentSearchAdapter.notifyDataSetChanged();

            // Update visibility
            updateRecentSearchVisibility();
        }
    }

    private void saveAllRecentSearches() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(RECENT_SEARCHES_KEY, new HashSet<>(recentSearches));
        editor.apply();
    }

    private void saveRecentSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        String trimmedQuery = query.trim().toLowerCase(); // Convert to lowercase

        // Remove if already exists (case insensitive)
        for (int i = 0; i < recentSearches.size(); i++) {
            if (recentSearches.get(i).toLowerCase().equals(trimmedQuery)) {
                recentSearches.remove(i);
                break;
            }
        }

        // Add to beginning
        recentSearches.add(0, query); // Store original case for display

        // Keep only last 5 searches
        if (recentSearches.size() > 5) {
            recentSearches.remove(recentSearches.size() - 1);
        }

        // Save to SharedPreferences
        saveAllRecentSearches();

        recentSearchAdapter.notifyDataSetChanged();
    }
    private ArrayList<String> getRecentSearches() {
        Set<String> savedSearches = sharedPreferences.getStringSet(RECENT_SEARCHES_KEY, new HashSet<>());
        return new ArrayList<>(savedSearches);
    }

    private void updateRecentSearchVisibility() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            if (!recentSearches.isEmpty()) {
                recentSearchContainer.setVisibility(View.VISIBLE);
                recyclerViewSearchProduct.setVisibility(View.GONE);
            } else {
                recentSearchContainer.setVisibility(View.GONE);
                recyclerViewSearchProduct.setVisibility(View.GONE);
            }
        } else {
            recentSearchContainer.setVisibility(View.GONE);
            recyclerViewSearchProduct.setVisibility(View.VISIBLE);
        }
    }

    private void onRecentSearchClicked(String searchText) {
        searchView.setText(searchText);
        searchQuery = searchText;
        searchProduct(searchText, false);
        saveRecentSearch(searchText);
        updateRecentSearchVisibility();
    }

    public void clearRecentSearches() {
        recentSearches.clear();
        sharedPreferences.edit().remove(RECENT_SEARCHES_KEY).apply();
        recentSearchAdapter.notifyDataSetChanged();
        updateRecentSearchVisibility();
    }

    public void searchProduct(String query, boolean applyFilters) {
        recentSearchContainer.setVisibility(View.GONE);
        recyclerViewSearchProduct.setVisibility(View.VISIBLE);
        searchProductArrayList.clear();
        originalProductArrayList.clear();
        HashSet<String> addedProductIds = new HashSet<>();
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");
        int[] validCount = {0};
        int[] dummyCount = {0};
        int[] queryCount = {0};
        int totalQueries = 3;

        // Helper to process snapshots
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String queryType = snapshot.getRef().toString().contains("productName") ? "productName" :
                        snapshot.getRef().toString().contains("productBrandName") ? "brandName" : "unknown";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        if (isValidProduct(product) && !addedProductIds.contains(product.getProductId())) {
                            originalProductArrayList.add(product);
                            addedProductIds.add(product.getProductId());
                            validCount[0]++;
                            Log.d("SearchProduct", "Added Product ID: " + product.getProductId() + " from " + queryType);
                        } else {
                            dummyCount[0]++;
                        }
                    }
                }
                queryCount[0]++;
                Log.d("SearchProduct", "Query completed: " + queryType + ", Valid: " + validCount[0] + ", Dummy: " + dummyCount[0]);
                if (queryCount[0] == totalQueries) {
                    Log.d("SearchProduct", "All queries done for '" + query + "', Total Products: " + originalProductArrayList.size() + ", Valid: " + validCount[0] + ", Dummy: " + dummyCount[0]);
                    if (applyFilters) {
                        filterData(gender, colour, brand, price, discount);
                    } else {
                        searchProductArrayList.clear();
                        searchProductArrayList.addAll(originalProductArrayList);
                        updateRecyclerView();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String queryType = error.getDetails().contains("productName") ? "productName" :
                        error.getDetails().contains("productBrandName") ? "brandName" : "unknown";
                Log.e("SearchProduct", "Query cancelled (" + queryType + "): " + error.getMessage());
                queryCount[0]++;
                if (queryCount[0] == totalQueries) {
                    Log.d("SearchProduct", "All queries done (with cancellation) for '" + query + "', Total Products: " + originalProductArrayList.size());
                    if (applyFilters) {
                        filterData(gender, colour, brand, price, discount);
                    } else {
                        searchProductArrayList.clear();
                        searchProductArrayList.addAll(originalProductArrayList);
                        updateRecyclerView();
                    }
                }
            }
        };

        // Search by product name
        Query productNameQuery = productRef.orderByChild("productName").startAt(query).endAt(query + "\uf8ff");
        productNameQuery.addListenerForSingleValueEvent(listener);

        // Search by brand name
        Query brandNameQuery = productRef.orderByChild("productBrandName").startAt(query).endAt(query + "\uf8ff");
        brandNameQuery.addListenerForSingleValueEvent(listener);

        // Search by keywords
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataSnapshot searchKeywordNode = dataSnapshot.child("searchKeyWord");
                    for (DataSnapshot keywordSnapshot : searchKeywordNode.getChildren()) {
                        String keyword = keywordSnapshot.getValue(String.class);
                        if (keyword != null && keyword.toLowerCase().contains(query.toLowerCase())) {
                            Product product = dataSnapshot.getValue(Product.class);
                            if (product != null) {
                                if (isValidProduct(product) && !addedProductIds.contains(product.getProductId())) {
                                    originalProductArrayList.add(product);
                                    addedProductIds.add(product.getProductId());
                                    validCount[0]++;
                                    Log.d("SearchProduct", "Added Product ID: " + product.getProductId() + " from keyword query");
                                } else {
                                    dummyCount[0]++;
                                }
                            }
                            break;
                        }
                    }
                }
                queryCount[0]++;
                Log.d("SearchProduct", "Keyword Query: " + query + ", Valid: " + validCount[0] + ", Dummy: " + dummyCount[0]);
                if (queryCount[0] == totalQueries) {
                    Log.d("SearchProduct", "All queries done for '" + query + "', Total Products: " + originalProductArrayList.size() + ", Valid: " + validCount[0] + ", Dummy: " + dummyCount[0]);
                    if (applyFilters) {
                        filterData(gender, colour, brand, price, discount);
                    } else {
                        searchProductArrayList.clear();
                        searchProductArrayList.addAll(originalProductArrayList);
                        updateRecyclerView();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchProduct", "Keyword query cancelled: " + error.getMessage());
                queryCount[0]++;
                if (queryCount[0] == totalQueries) {
                    Log.d("SearchProduct", "All queries done (with cancellation) for '" + query + "', Total Products: " + originalProductArrayList.size());
                    if (applyFilters) {
                        filterData(gender, colour, brand, price, discount);
                    } else {
                        searchProductArrayList.clear();
                        searchProductArrayList.addAll(originalProductArrayList);
                        updateRecyclerView();
                    }
                }
            }
        });
    }

    private boolean isValidProduct(Product product) {
        // Validate product to exclude dummy or invalid records
        boolean isValid = product != null &&
                product.getProductId() != null && !product.getProductId().isEmpty() &&
                !product.getProductId().toLowerCase().contains("dummy") &&
                product.getProductName() != null && !product.getProductName().isEmpty() &&
                !product.getProductName().toLowerCase().contains("dummy") &&
                product.getProductBrandName() != null && !product.getProductBrandName().isEmpty() &&
                !product.getProductBrandName().toLowerCase().contains("dummy") &&
                product.getProductColour() != null && !product.getProductColour().isEmpty() &&
                product.getSellingPrice() != null && !product.getSellingPrice().isEmpty() &&
                product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty() &&
                product.getProductImages() != null && !product.getProductImages().isEmpty();
        Log.d("SearchProduct", "Product ID: " + (product != null ? product.getProductId() : "null") + ", Valid: " + isValid);
        return isValid;
    }
    public void filterData(String gender, ArrayList<String> colour, String brand, ArrayList<PriceRange> price, ArrayList<DiscountRange> discount) {
        searchProductArrayList.clear();
        HashSet<String> productId = new HashSet<>();

        for (Product product : originalProductArrayList) {
            boolean passFilter = true;

            // Gender filter
            if (gender != null && !gender.isEmpty()) {
                passFilter = passFilter && ((gender.equals("male") && product.getProductSuitFor().startsWith("Men")) ||
                        (gender.equals("female") && product.getProductSuitFor().startsWith("Women")));
            }

            // Colour filter
            if (colour != null && !colour.isEmpty()) {
                passFilter = passFilter && colour.contains(product.getProductColour());
            }

            // Brand filter
            if (brand != null && !brand.isEmpty()) {
                passFilter = passFilter && brand.equals(product.getProductBrandName());
            }

            // Price filter
            if (price != null && !price.isEmpty()) {
                try {
                    passFilter = passFilter && isProductInPriceRange(Long.parseLong(product.getSellingPrice()), price);
                } catch (NumberFormatException e) {
                    passFilter = false;
                }
            }

            // Discount filter
            if (discount != null && !discount.isEmpty()) {
                try {
                    passFilter = passFilter && isDiscountInRange(Long.parseLong(product.getOriginalPrice()),
                            Long.parseLong(product.getSellingPrice()), discount);
                } catch (NumberFormatException e) {
                    passFilter = false;
                }
            }

            if (passFilter && !productId.contains(product.getProductId())) {
                searchProductArrayList.add(product);
                productId.add(product.getProductId());
            }
        }

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        searchProductAdapter.notifyDataSetChanged();
        txtFilter.setVisibility(searchProductArrayList.isEmpty() ? View.GONE : View.VISIBLE);
        showToast(searchProductArrayList.size() + " product" + (searchProductArrayList.size() != 1 ? "s" : "") + " found");
    }

    private void showToast(String message) {
        // Cancel existing toast if any
        if (currentToast != null) {
            currentToast.cancel();
        }
        // Show new toast
        currentToast = StyleableToast.makeText(this, message, R.style.UptrendToast);
        currentToast.show();
        // Auto-dismiss after 2 seconds
        toastHandler.postDelayed(() -> {
            if (currentToast != null) {
                currentToast.cancel();
                currentToast = null;
            }
        }, TOAST_DURATION);
    }

    private boolean isProductInPriceRange(long price, ArrayList<PriceRange> priceRanges) {
        for (PriceRange range : priceRanges) {
            try {
                if (price >= Long.parseLong(range.getMinPrice()) && price <= Long.parseLong(range.getMaxPrice())) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private boolean isDiscountInRange(long originalPrice, long sellingPrice, ArrayList<DiscountRange> discountRanges) {
        for (DiscountRange range : discountRanges) {
            try {
                double discountPer = calculateDiscountPercentage(originalPrice, sellingPrice);
                if (discountPer >= Double.parseDouble(range.getMinDiscount()) &&
                        discountPer <= Double.parseDouble(range.getMaxDiscount())) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public double calculateDiscountPercentage(long originalPrice, long sellingPrice) {
        if (originalPrice == 0) return 0;
        double discount = originalPrice - sellingPrice;
        return (discount / originalPrice) * 100;
    }

    @Override
    public void ItemOnClickListener(String productId) {
        Intent i = new Intent(this, open_product.class);
        i.putExtra("productId", productId);
        i.putExtra("activityName", "searchProduct");
        i.putExtra("searchQuery", searchQuery);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        if (currentToast != null) {
            currentToast.cancel();
        }
        toastHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_QUERY, searchQuery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String voiceSearchText = result.get(0);
                searchView.setText(voiceSearchText);
                // Save voice search as recent search
                saveRecentSearch(voiceSearchText);
                searchProduct(voiceSearchText, false);
                hideKeyboard();
            }
        }
    }

}