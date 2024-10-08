package com.example.techmarket_finalproject.Utilities;

import android.content.Context;

import com.example.techmarket_finalproject.Interfaces.GenericCallBack;
import com.example.techmarket_finalproject.Models.CategoryEnum;
import com.example.techmarket_finalproject.Models.Product;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProductManager {

    private static final Map<String, Product> productCache = new HashMap<>();
    private static final List<Product> allProducts = new ArrayList<>();
    private static boolean isInitialized = false;
    private static boolean productDeleted = false;

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static void initialize(Context context, GenericCallBack<ArrayList<Product>> callback) {
        if (!isInitialized) {
            DatabaseManager.getAllProductsFromDatabase(new GenericCallBack<ArrayList<Product>>() {
                @Override
                public void onResponse(ArrayList<Product> response) {
                    productCache.clear();
                    allProducts.clear();
                    for (Product product : response) {
                        productCache.put(product.getProductId(), product);
                        allProducts.add(product);
                    }
                    isInitialized = true;
                    callback.onResponse(response);
                }

                @Override
                public void onFailure(DatabaseError error) {
                    callback.onFailure(error);
                }
            });
        } else {
            callback.onResponse(new ArrayList<>(productCache.values()));
        }
    }

    public static Product getProductById(String productId) {
        return productCache.get(productId);
    }

    public static ArrayList<Product> getAllProducts() {
        return new ArrayList<>(productCache.values());
    }

    public static ArrayList<Product> getProductsByCategory(CategoryEnum category) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product product : productCache.values()) {
            if (product.getCategory() == category) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    public static List<Product> searchProductsByName(String query) {
        List<Product> result = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getTitle().toLowerCase().trim().contains(query.toLowerCase())) {
                result.add(product);
            }
        }
        return result;
    }

    public static boolean isProductDeleted() {
        return productDeleted;
    }

    public static void setProductDeleted(boolean productDeleted) {
        ProductManager.productDeleted = productDeleted;
    }

    public static void removeProductById(String productId) {
        productCache.remove(productId);
        for (Iterator<Product> iterator = allProducts.iterator(); iterator.hasNext(); ) {
            Product product = iterator.next();
            if (product.getProductId().equals(productId)) {
                iterator.remove();
                break;
            }
        }
    }

}
