package org.gridgain.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteRunnable;

public class BuyerTotalSpendSchema implements IgniteRunnable {

    private Ignite ignite_;

    public BuyerTotalSpendSchema(Ignite ignite) {ignite_ = ignite;};

    @Override
    public void run() {
        IgniteCache<Integer, Double> buyerTotalSpend = ignite_.cache("BUYER_TOTAL_SPEND");
        if(null == buyerTotalSpend) {
            IgniteCache utilityCache = ignite_.getOrCreateCache(new CacheConfiguration("utilityCache"));

            utilityCache.query(new SqlFieldsQuery(
                    "CREATE TABLE BUYER_TOTAL_SPEND (buyer_id int PRIMARY KEY, total_spend double) WITH \"backups=1, atomicity=transactional, cache_name=BuyerTotalSpend\"").
                    setSchema("PUBLIC")).getAll();

            SqlFieldsQuery query = new SqlFieldsQuery("INSERT INTO BUYER_TOTAL_SPEND (buyer_id, total_spend) VALUES (?,?)").
                    setSchema("PUBLIC");

            utilityCache.query(query.setArgs(1, 0.0));
            utilityCache.query(query.setArgs(2, 0.0));
            utilityCache.query(query.setArgs(3, 0.0));
            utilityCache.query(query.setArgs(4, 0.0));
            utilityCache.query(query.setArgs(5, 0.0));
            utilityCache.query(query.setArgs(6, 0.0));
        }
    }

}
