package pl.plgrid.unicore.common.testing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * The class file to actually carry out the validations
 * for the various validate annotations we have declared
 *
 * @author Y.Kamesh Rao
 */
public class TestingGlobalStateProcessor {
    /**
     * Method to process all the annotations
     *
     * @param obj The name of the object where
     *            annotations are to be identified and
     *            processed
     */
    public static void processAnnotations(Object obj) {
        try {
            Class cl = obj.getClass();

            // Checking all the fields for annotations
            for (Field f : cl.getDeclaredFields()) {
                // Since we are Validating fields, there may be many
                // NullPointer and similar exceptions thrown,
                // so we need  to catch them
                try {
                    // Processing all the annotations on a single field
                    for (Annotation a : f.getAnnotations()) {
                        // Checking for a NullValueValidate annotation
                        if (a.annotationType() == TestingGlobalStateAnnotation.class) {
                            TestingGlobalStateAnnotation nullVal = (TestingGlobalStateAnnotation) a;
                            System.out.println("Processing the field : " + nullVal.paramName());

                            // Setting the field to be accessible from our class
                            // is it is a private member of the class under processing
                            // (which its most likely going to be)
                            // The setAccessible method will not work if you have
                            // Java SecurityManager configured and active.
                            f.setAccessible(true);

                            // Checking the field for a null value and
                            // throwing an exception is a null value encountered.
                            // The get(Object obj) method on Field class returns the
                            // value of the Field for the Object which is under test right now.
                            // In other words, we need to send 'obj' as the object
                            // to this method since we are currently processing the
                            // annotations present on the 'obj' Object.
                            if (f.get(obj) == null) {
                                throw new NullPointerException("'The value of the field " + f.toString() + " can't be NULL.");
                            } else
                                System.out.println("Value of the Object : " + f.get(obj));
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
