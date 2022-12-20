public class dtmf_code {
    private String name;
    private String code;
    private String category;

    public dtmf_code(String name, String code, String category) {
        this.name = name;
        this.code = code;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "dtmf_code{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
