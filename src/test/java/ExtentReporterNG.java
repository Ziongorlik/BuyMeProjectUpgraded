import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/***
 * The class manages the extent reports instance using TestNG
 */
public class ExtentReporterNG {
    private static ExtentReports extent;

    /***
     * Creates a single instance of a Extent Report.
     * @return The instance of the Extent Report.
     */
    public static ExtentReports getExtentReportInstance(String reportName, String fileName){
        if (extent == null){
            String path = System.getProperty("user.dir") + "\\reports\\" + fileName;
            ExtentSparkReporter reporter = new ExtentSparkReporter(path);
            reporter.config().setReportName(reportName);
            reporter.config().setDocumentTitle("Test Results");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Environment","Production");
            extent.setSystemInfo("Tester","Zion G.");
        }
        return extent;
    }
}