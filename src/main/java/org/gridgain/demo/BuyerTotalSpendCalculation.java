package org.gridgain.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
public class BuyerTotalSpendCalculation implements IgniteRunnable {

    private static final SqlFieldsQuery UPDATE_QUERY = new SqlFieldsQuery("UPDATE BUYER_TOTAL_SPEND SET total_spend=? WHERE buyer_id=?").setSchema("PUBLIC");

    private Ignite ignite_;

    public BuyerTotalSpendCalculation(Ignite ignite) {
        ignite_ = ignite;
    }

    public void run() {
        IgniteCache utilityCache = ignite_.getOrCreateCache(new CacheConfiguration("utilityCache"));
        FieldsQueryCursor<List<?>> results = utilityCache.query(
                new SqlFieldsQuery("SELECT buyer_id, (order_quantity * bid_price) AS buyer_total FROM TRADE ORDER BY buyer_id;")
                        .setSchema("PUBLIC")
                        .setLocal(true));

        HashMap<Integer, Double> accumulation = new HashMap<Integer, Double>();
        Iterator<List<?>> iter = results.iterator();
        while(iter.hasNext()) {
            List<?> row = iter.next();
            Integer currentBuyer = ((Integer) row.get(0));
            Double currentTotal = ((Double) row.get(1));
            Double saveTotal = accumulation.get(currentBuyer);
            if(null == saveTotal) {
                accumulation.put(currentBuyer, currentTotal);
            } else {
                Double summedTotal = saveTotal.doubleValue() + currentTotal.doubleValue();
                accumulation.put(currentBuyer, summedTotal);
            }
        }
        Set<Integer> keys = accumulation.keySet();
        for (Integer key : keys) {
            Double value = accumulation.get(key);
            saveResultsFor(key, value);
        }
    }

    private void saveResultsFor(int currentBuyer, double currentTotal) {
        IgniteCache utilityCache = ignite_.getOrCreateCache(new CacheConfiguration("utilityCache"));
        utilityCache.query(UPDATE_QUERY.setArgs(currentTotal, currentBuyer));
        ignite_.log().info("Buyer " + currentBuyer + " updated to " + currentTotal + " total spend");
        System.out.println("Buyer " + currentBuyer + " updated to " + currentTotal + " total spend");
    }

}
