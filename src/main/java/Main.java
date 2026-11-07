import utils.PairMap;
import utils.SelfMap;

public class Main {
  public static void main(String args[]) {
    System.out.println("Hello World!");
    // PairMap<String, Integer> map = new PairMap<>();
    // map.put("Josh", 4);
    // map.put("Bracket", 1);
    // System.out.println(map.get("Josh"));
    // System.out.println(map.get("Bracket"));
    // System.out.println(map.getReverse(4));
    // System.out.println(map.getReverse(1));

    SelfMap<Integer> map = new SelfMap<>();
    map.put(1, 1);
    map.put(2, 3);
    System.out.println(map.get(1));
    System.out.println(map.get(2));
    System.out.println(map.get(3));
    System.out.println(map.size());
  }
};
