import java.util.Arrays;

public enum TestResultValue {
    ERROR(-1),
    UNDEFINED(0),
    PASS(1),
    WARNING(2),
    FAIL(3);

    private final int code;

    TestResultValue(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TestResultValue fromCode(int code) {
            return Arrays.stream(values())
                    .filter(r -> r.code == code)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown test result code: " + code));
    }

}
