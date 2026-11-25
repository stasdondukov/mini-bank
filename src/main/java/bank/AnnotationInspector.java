package bank;

import bank.application.annotation.Audited;
import bank.application.annotation.RequiresRole;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class AnnotationInspector {
    private final PrintStream output;

    public AnnotationInspector() {
        this(System.out);
    }

    public AnnotationInspector(PrintStream output) {
        this.output = output;
    }

    public void inspect(Class<?> type) {
        output.println("Class: " + type.getName());
        output.println("Audited: " + type.isAnnotationPresent(Audited.class));

        Method[] methods = type.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::toGenericString));

        output.println("Declared methods:");
        for (Method method : methods) {
            output.println(method.toGenericString());
        }

        output.println("Required roles:");
        for (Method method : methods) {
            RequiresRole role = method.getAnnotation(RequiresRole.class);
            if (role != null) {
                output.println(method.getName() + " -> " + role.value());
            }
        }
    }
}
