package org.gridgain.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;

public class BuyerTotalSpendOperations {

    private static final String HELP = new String("-help");
    private static final String SCHEMA = new String("-create_schema");
    private static final String EXEC_ONCE = new String("-exec_once");

    private ClusterClient clusterClient;

    public static void main(String[] args) {
        if(args.length < 1) {
            displayHelp();
        } else if(!isValidArgument(args[0])) {
            displayHelp();
        } else if (HELP.equals(args[0])) {
            displayHelp();
        } else {
            try (ClusterClient clusterClient = new ClusterClient()) {
                Ignite ignite = clusterClient.startClient();
                IgniteCompute compute = ignite.compute();
                if (SCHEMA.equals(args[0])) {
                    BuyerTotalSpendSchema schema = new BuyerTotalSpendSchema(ignite);
                    compute.run(schema);
                } else if (EXEC_ONCE.equals(args[0])) {
                    BuyerTotalSpendCalculation calc = new BuyerTotalSpendCalculation(ignite);
                    compute.broadcast(calc);
                }
            }
        }
    }

    private static boolean isValidArgument(String argument) {
        boolean result = false;
        if(HELP.equals(argument) || SCHEMA.equals(argument) || EXEC_ONCE.equals(argument)) {
            result = true;
        }
        return result;
    }

    private static void displayHelp() {
        System.out.println("Invalid invocation!");
        System.out.println("*******************************************************************************");
        System.out.println("Valid arguments are:");
        System.out.println("  -help which displays program usage");
        System.out.println("  -create_schema which creates and populates the BUYER_TOTAL_SPEND table.");
        System.out.println("  -exec_once which executes a broadcast for local calculation for all buyers.");
        System.out.println("*******************************************************************************");
        System.out.println("Example invocations follow");
        System.out.println("  java -cp buyer-total-spend-compute.jar org.gridgain.demo.BuyerTotalSpendOperations -help");
        System.out.println("  java -cp buyer-total-spend-compute.jar org.gridgain.demo.BuyerTotalSpendOperations -create_schema");
        System.out.println("  java -cp buyer-total-spend-compute.jar org.gridgain.demo.BuyerTotalSpendOperations -exec_once");
        System.out.println("*******************************************************************************");
    }

}
