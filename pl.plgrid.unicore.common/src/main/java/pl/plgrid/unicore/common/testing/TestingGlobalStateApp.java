package pl.plgrid.unicore.common.testing;

/**
 * Main class to test the Annotations
 *
 * @author Y.Kamesh Rao
 */
public class TestingGlobalStateApp {
    @TestingGlobalStateAnnotation(paramName = "testVar1")
    private String testVar1;
    @TestingGlobalStateAnnotation(paramName = "testVar2")
    private String testVar2;


    public TestingGlobalStateApp() {
        testVar2 = "Testing the Null Value Validation...It Works...!";

        // Calling the processor to process the annotations applied
        // on this class object.
        TestingGlobalStateProcessor.processAnnotations(this);
    }

    public static void main(String args[]) {
        TestingGlobalStateApp ae = new TestingGlobalStateApp();
    }
}


