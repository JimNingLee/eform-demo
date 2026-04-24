package com.cabsoft.config;

public class Config {

    private String configString = null;
    private String parsedConfigString = null;
    private String Name = "";

    private Config() {
    }

    /**
     *
     * @param configString
     * @return
     * @throws Exception
     */
    public static Config newInstance(String configString) throws Exception {
        Config instance = buildConfig(configString);
        return instance;
    }

    private static Config buildConfig(String configString) throws Exception {
        StringBuffer parsedConfigString = new StringBuffer();
        int len = configString.length();
        boolean isCommentMultiLine = false;
        boolean isCommentSingleLine = false;

        for (int i = 0; i < len; i++) {
            char c = configString.charAt(i);

            // Checking MultiLine Comment
            if (isCommentMultiLine == false) {
                if (c == '/' && configString.charAt(i + 1) == '*') {
                    isCommentMultiLine = true;
                    continue;
                }
            } else {
                if (c == '/' && configString.charAt(i - 1) == '*') {
                    isCommentMultiLine = false;
                }
                continue;
            }

            // Checking SingleLine Comment
            if (isCommentSingleLine == false) {
                if (c == '-' && configString.charAt(i + 1) == '-') {
                    isCommentSingleLine = true;
                    continue;
                }
//                if ((c == '/' && configString.charAt(i + 1) == '/') || (c == '-' && configString.charAt(i + 1) == '-')) {
//                    isCommentSingleLine = true;
//                    continue;
//                }
            } else {
                if (c == '\n' || c == '\r') {
                    parsedConfigString.append(c);
                    isCommentSingleLine = false;
                }
                continue;
            }
             parsedConfigString.append(c);
        }

        Config query = new Config();
        query.setConfigString(configString.trim());
        query.setParsedConfigString(parsedConfigString.toString().trim());
        return query;
    }

    /**
     *
     * @param configString
     */
    protected void setConfigString(String configString) {
        this.configString = configString;
    }

    /**
     *
     * @param parsedConfigString
     */
    protected void setParsedConfigString(String parsedConfigString) {
        this.parsedConfigString = parsedConfigString;
    }

    /**
     *
     * @return
     */
    public String getConfigString() {
        return configString;
    }

    /**
     *
     * @return
     */
    public String getParsedConfigString() {
        return parsedConfigString;
    }

    public boolean getBoolean(){
        boolean value = true;
        try {
            value = Boolean.valueOf(parsedConfigString).booleanValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal Boolean Key : " + Name);
        }
        return value;
    }

    public String getString(){
        return parsedConfigString;
    }

    public String getValue(){
        return parsedConfigString;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return Name;
    }

    /**
     *
     * @param Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

}
