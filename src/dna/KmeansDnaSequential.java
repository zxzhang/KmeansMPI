package dna;

import util.Util;
import data.DnaPoint;

/**
 * @author zhengxiong
 *
 *         DNA data sequential implementation
 */
public class KmeansDnaSequential {

  public static final int thet = 1;

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("java KmeansDnaSequential <input_file> <cluster_number>");
      return;
    }

    long start = System.currentTimeMillis();

    KmeansDna obj = new KmeansDna(Integer.parseInt(args[1]), args[0]);
    obj.clusterInitialize();
    DnaPoint[] result = runKmeans(obj);

    long end = System.currentTimeMillis();

    Util.printDnaResult(result, obj.getClusterNumber());
    System.out.println("run time: " + (end - start) + "ms");
  }

  /**
   * @param obj
   * @return
   */
  private static DnaPoint[] runKmeans(KmeansDna obj) {
    int round = 0;

    while (true) {

      obj.updateGroup(0, obj.getDataSize());

      // printResult(groupInfo);

      DnaPoint[] newCentroid = obj.updateCentroid(0, obj.getDataSize());
      obj.updateCentroidsValues(newCentroid);
      double diff = obj.calculateDiff(newCentroid);

      System.out.println("Round: " + ++round + "\tDiff: " + diff);

      if (diff < thet) {
        return obj.getData();
      } else {
        obj.setCentroid(newCentroid);
      }
    }
  }
}
