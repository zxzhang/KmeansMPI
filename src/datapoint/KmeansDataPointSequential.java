package datapoint;

import util.Util;
import data.DataPoint;

/**
 * @author zhengxiong
 * 
 *         Point data Sequential implementation
 */
public class KmeansDataPointSequential {

  public static final double thet = 0.1;

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("java KmeansDataPointSequential <input_file> <cluster_number>");
      return;
    }

    long start = System.currentTimeMillis();

    KmeansDataPoint obj = new KmeansDataPoint(Integer.parseInt(args[1]), args[0]);
    obj.clusterInitialize();
    DataPoint[] result = runKmeans(obj);

    long end = System.currentTimeMillis();

    Util.printResult(result, obj.getClusterNumber());
    System.out.println("run time: " + (end - start) + "ms");
  }

  /**
   * @param obj
   * @return
   */
  private static DataPoint[] runKmeans(KmeansDataPoint obj) {
    int round = 0;

    while (true) {

      obj.updateGroup(0, obj.getDataSize());

      // printResult(groupInfo);

      DataPoint[] newCentroid = obj.updateCentroid(0, obj.getDataSize());
      obj.divide(newCentroid);
      double diff = obj.calculateDiff(newCentroid);

      System.out.println("Round: " + ++round + "\t" + "Diff: " + diff);

      if (diff < thet) {
        return obj.getData();
      } else {
        obj.setCentroid(newCentroid);
      }
    }
  }
}
