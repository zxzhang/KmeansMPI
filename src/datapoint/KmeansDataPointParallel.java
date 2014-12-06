package datapoint;

import util.Util;
import data.DataPoint;
import mpi.MPI;
import mpi.MPIException;

/**
 * @author zhengxiong
 *
 *         Point data parallel implementation
 */
public class KmeansDataPointParallel {

  public static final double thet = 0.1;

  /**
   * @param args
   * @throws MPIException
   */
  public static void main(String[] args) throws MPIException {
    if (args.length != 2) {
      System.out.println("java KmeansDataPointParallel <input> <cluster>");
      return;
    }

    long startTime = System.currentTimeMillis();

    MPI.Init(args);

    int myrank = MPI.COMM_WORLD.Rank();
    // int cluster = Integer.parseInt(args[1]);
    KmeansDataPoint kmeans = new KmeansDataPoint(Integer.parseInt(args[1]), args[0]);
    int size = kmeans.getDataSize();
    boolean[] run = new boolean[1];
    run[0] = true;

    int len = (int) Math.ceil(((double) size) / ((double) MPI.COMM_WORLD.Size()));
    int start = myrank * len;
    int end = Math.min((myrank + 1) * len, size);

    if (myrank == 0) {
      kmeans.clusterInitialize();
    }

    int round = 0;

    while (run[0]) {

      MPI.COMM_WORLD.Bcast(kmeans.getCentroid(), 0, kmeans.getClusterNumber(), MPI.OBJECT, 0);
      kmeans.updateGroup(start, end);
      DataPoint[] cent = kmeans.updateCentroid(start, end);

      if (myrank == 0) {

        for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
          DataPoint[] buff = new DataPoint[kmeans.getClusterNumber()];
          MPI.COMM_WORLD.Recv(buff, 0, kmeans.getClusterNumber(), MPI.OBJECT, i, 1);

          for (int j = 0; j < kmeans.getClusterNumber(); j++) {
            cent[j].setCnt(cent[j].getCnt() + buff[j].getCnt());
            cent[j].addDataPoint(buff[j]);
          }
        }

        kmeans.divide(cent);
        double diff = kmeans.calculateDiff(cent);

        // /////////////////////////////////////////////////////////////
        System.out.println("Round: " + ++round + "\tDiff: " + diff);

        if (diff < thet) {
          run[0] = false;
          // return kmeans.getData();
        } else {
          kmeans.setCentroid(cent);
        }

      } else {

        MPI.COMM_WORLD.Send(cent, 0, kmeans.getClusterNumber(), MPI.OBJECT, 0, 1);
      }

      MPI.COMM_WORLD.Bcast(run, 0, 1, MPI.BOOLEAN, 0);
    }

    DataPoint[] result = kmeans.getData();

    if (myrank == 0) {

      for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
        DataPoint[] buff = new DataPoint[len];
        MPI.COMM_WORLD.Recv(buff, 0, len, MPI.OBJECT, i, 2);

        for (int j = 0; j < len && end < result.length; j++) {
          result[end++] = buff[j];
        }
      }

      long endTime = System.currentTimeMillis();
      Util.printResult(result, kmeans.getClusterNumber());

      System.out.println("run time: " + (endTime - startTime) + "ms");
    } else {

      MPI.COMM_WORLD.Send(result, start, end - start, MPI.OBJECT, 0, 2);
    }

    MPI.Finalize();
  }
}
