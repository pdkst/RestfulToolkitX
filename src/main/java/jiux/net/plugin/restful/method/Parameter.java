package jiux.net.plugin.restful.method;

/**
 * method params
 */
public class Parameter {

  private String paramType;
  private String paramName;
  private String defaultValue = null;
  private boolean required = false;
  private boolean requestBodyFound = false;

  private boolean isGeneric = false;

  public Parameter() {}

  public Parameter(String paramType, String paramName) {
    this.paramType = paramType;
    this.paramName = paramName;
  }

  public Parameter(String paramType, String paramName, boolean isGeneric) {
    this.paramType = paramType;
    this.paramName = paramName;
    this.isGeneric = isGeneric;
  }

  public Parameter(String paramType, String paramName, String defaultValue) {
    this.paramType = paramType;
    this.paramName = paramName;
    this.defaultValue = defaultValue;
  }

  /*   public Parameter required() {
           this.required = true;
           return this;
       }
   */
  public String getParamType() {
    return paramType;
  }

  public void setParamType(String paramType) {
    this.paramType = paramType;
  }

  public String getParamName() {
    return paramName;
  }

  public void setParamName(String paramName) {
    this.paramName = paramName;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean isRequired() {
    return required;
  }

  public Parameter setRequired(boolean required) {
    this.required = required;
    return this;
  }

  public boolean isRequestBodyFound() {
    return requestBodyFound;
  }

  public void setRequestBodyFound(boolean requestBodyFound) {
    this.requestBodyFound = requestBodyFound;
  }

  public Parameter requestBodyFound(boolean requestBodyFound) {
    this.requestBodyFound = requestBodyFound;
    return this;
  }

  public String getShortTypeName() {
    //todo : List

    String shortName = paramType.substring(
      paramType.lastIndexOf(".") + 1,
      paramType.length()
    );
    return shortName;
  }

  public boolean isGeneric() {
    return isGeneric;
  }

  public void setGeneric(boolean generic) {
    isGeneric = generic;
  }
}
