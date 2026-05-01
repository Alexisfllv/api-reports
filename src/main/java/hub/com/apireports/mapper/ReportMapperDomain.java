package hub.com.apireports.mapper;


public final class ReportMapperDomain {

    public static String toLabel(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String cleanText = text.toLowerCase().replace("_", " ").trim();
        String[] words = cleanText.split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String word : words) {

            if (isConnector(word)) {
                result.append(word).append(" ");
            } else {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private static boolean isConnector(String word) {
        return word.equals("de")
                || word.equals("del")
                || word.equals("la")
                || word.equals("las")
                || word.equals("los")
                || word.equals("y");
    }
}
