package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.PortedModels;

/** Prints the simple names of the ported model classes, for compare-models.sh. */
public final class PortedModelNames {
    public static void main(String[] args) {
        for (String name : PortedModels.all()) {
            System.out.println(name.substring(name.lastIndexOf('.') + 1));
        }
    }
}
