package rsi;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.rmi.Naming;

public class MyClient {

    public static void main(String[] args) {
        var pairs = showMenu();
        var adresses = getWorkerAdresses(2);

        for (int i = 0; i < pairs.size(); i++) {
            var pair = pairs.get(i);
            ReturnType result = processPair(pair, adresses.get(i % 2));

            if (result != null)
                System.out.println("Med: " + result.med +
                        "\nCount: " + result.count +
                        "\nMax: " + result.max);
            else
                System.out.println("Error for pair: " + pair.x + " " + pair.y);
        }
    }

    private static List<Pair> showMenu() {
        var scanner = new Scanner(System.in);
        System.out.print("Podaj ile zestawów danych chcesz wprowadzić: ");
        var nSets = scanner.nextInt();

        var pairs = new ArrayList<Pair>();
        for (int i = 0; i < nSets; i++) {
            try {
                System.out.print("\nPodaj x: ");
                var from = scanner.nextLong();
                System.out.print("Podaj y: ");
                var to = scanner.nextLong();
                if (from >= to) {
                    System.out.println("X nie może być większe od Y. Spróbuj ponownie");
                    i -= 1;
                } else{
                    pairs.add(new Pair(from, to));
                }
            } catch (InputMismatchException ex) {
                scanner = new Scanner(System.in);
                i -= 1;
                System.out.println("Podana wartość musi być liczbą typu long");
            }
        }

        return pairs;
    }

    private static ReturnType processPair(Pair pair, String address) {
        try {
            var worker = (Task) Naming.lookup(address);

            try {
                return worker.compute(pair.x, pair.y);
            } catch (Exception e) {
                System.err.println("Remote execution error");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Reference");
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> getWorkerAdresses(int nWorkers) {
        var addresses = new ArrayList<String>(nWorkers);
        for (int i = 0; i < nWorkers; i++) {
            addresses.add("//localhost/" + String.valueOf(i));
        }
        return addresses;
    }
}


