package tiamat.compiler.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import tiamat.compiler.PrefsProcessor;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forSourceString;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static tiamat.compiler.test.AssetsUtils.readString;

public class PrefsProcessorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void tableNameNotDefined() {
        JavaFileObject target = forSourceString("NoTableName", readString("NoTableName.java"));
        assert_().about(javaSource())
                .that(target)
                .processedWith(new PrefsProcessor())
                .compilesWithoutError();
    }

    @Test
    public void tableNameDuplicated() {
        expectRuntimeException("table name foo is already defined");
        JavaFileObject target = forSourceString("TableNameDuplicated", readString("TableNameDuplicated.java"));
        assert_().about(javaSource())
                .that(target)
                .processedWith(new PrefsProcessor())
                .failsToCompile();
    }

    private void expectRuntimeException(String message) {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(message);
    }
}
