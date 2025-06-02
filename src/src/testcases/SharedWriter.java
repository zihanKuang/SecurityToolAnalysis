package testcases;

public class SharedWriter {
    private StringBuilder sb = new StringBuilder();

    public void write(String input) {
        sb.setLength(0);
        sb.append(input);
        System.out.println(sb.toString());
    }
}
