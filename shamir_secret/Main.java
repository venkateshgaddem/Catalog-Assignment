import java.io.*;
import java.util.*;
import java.math.BigInteger;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Main {

    public static void main(String[] args) {
        String[] inputFiles = { "testcase1.json", "testcase2.json" };

        for (String currentFile : inputFiles) {
            try {
                JSONObject jsonData = (JSONObject) new JSONParser().parse(new FileReader(currentFile));
                JSONObject keyInfo = (JSONObject) jsonData.get("keys");
                int threshold = Integer.parseInt(keyInfo.get("k").toString());

                List<Integer> xValues = new ArrayList<>();
                List<Double> yValues = new ArrayList<>();

                for (Object entry : jsonData.keySet()) {
                    String keyStr = entry.toString();
                    if (!keyStr.equals("keys")) {
                        int xCoord = Integer.parseInt(keyStr);
                        JSONObject dataPoint = (JSONObject) jsonData.get(keyStr);
                        int numericBase = Integer.parseInt(dataPoint.get("base").toString());
                        String encodedValue = dataPoint.get("value").toString();
                        BigInteger decodedY = new BigInteger(encodedValue, numericBase);
                        double yCoord = decodedY.doubleValue();

                        xValues.add(xCoord);
                        yValues.add(yCoord);
                    }
                }

                double[][] interpolationPoints = new double[threshold][2];
                for (int i = 0; i < threshold; i++) {
                    interpolationPoints[i][0] = xValues.get(i);
                    interpolationPoints[i][1] = yValues.get(i);
                }

                double recoveredSecret = lagrangeInterpolation(interpolationPoints);
                System.out.println("Secret from " + currentFile + " = " + Math.round(recoveredSecret));

            } catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    public static double lagrangeInterpolation(double[][] coords) {
        double finalResult = 0.0;

        for (int i = 0; i < coords.length; i++) {
            double xi = coords[i][0], yi = coords[i][1];
            double term = yi;

            for (int j = 0; j < coords.length; j++) {
                if (i != j) {
                    double xj = coords[j][0];
                    term *= (-xj) / (xi - xj); // f(0)
                }
            }

            finalResult += term;
        }

        return finalResult;
    }
}