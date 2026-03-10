import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TransactionAnalyzer {

    public static List<int[]> findTwoSum(List<Transaction> list, int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : list) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public static List<int[]> findTwoSumWithinHour(List<Transaction> list, int target) {
        Map<Integer, List<Transaction>> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : list) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                for (Transaction prev : map.get(complement)) {
                    if (Math.abs(t.time - prev.time) <= 3600) {
                        result.add(new int[]{prev.id, t.id});
                    }
                }
            }

            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }

        return result;
    }

    public static List<List<Integer>> findKSum(List<Transaction> list, int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(list, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(List<Transaction> list, int k, int target, int start,
                                  List<Integer> current, List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0) return;

        for (int i = start; i < list.size(); i++) {
            Transaction t = list.get(i);

            current.add(t.id);
            backtrack(list, k - 1, target - t.amount, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void detectDuplicates(List<Transaction> list) {

        Map<String, Set<String>> map = new HashMap<>();

        for (Transaction t : list) {
            String key = t.amount + "-" + t.merchant;

            map.computeIfAbsent(key, k -> new HashSet<>()).add(t.account);
        }

        for (String key : map.keySet()) {
            Set<String> accounts = map.get(key);

            if (accounts.size() > 1) {
                String[] parts = key.split("-");
                System.out.println("Duplicate :- amount:" + parts[0] +
                        ", merchant:" + parts[1] +
                        ", accounts:" + accounts);
            }
        }
    }

    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1, 500, "StoreA", "acc1", 0));
        transactions.add(new Transaction(2, 300, "StoreB", "acc2", 900));
        transactions.add(new Transaction(3, 200, "StoreC", "acc3", 1800));
        transactions.add(new Transaction(4, 500, "StoreA", "acc2", 2000));

        List<int[]> pairs = findTwoSum(transactions, 500);

        for (int[] p : pairs) {
            System.out.println("TwoSum Pair: " + p[0] + ", " + p[1]);
        }

        List<int[]> windowPairs = findTwoSumWithinHour(transactions, 500);

        for (int[] p : windowPairs) {
            System.out.println("TwoSum within 1h: " + p[0] + ", " + p[1]);
        }

        List<List<Integer>> ksum = findKSum(transactions, 3, 1000);

        for (List<Integer> ids : ksum) {
            System.out.println("KSum: " + ids);
        }

        detectDuplicates(transactions);
    }
}