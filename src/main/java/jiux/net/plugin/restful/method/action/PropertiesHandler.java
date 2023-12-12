package jiux.net.plugin.restful.method.action;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

public class PropertiesHandler {

  public List<String> CONFIG_FILES = Arrays.asList("application", "bootstrap");
  public List<String> FILE_EXTENSIONS = Arrays.asList("properties", "yml");
  String SPRING_PROFILE = "spring.profiles.active";
  String placeholderPrefix = "${";
  String valueSeparator = ":";
  String placeholderSuffix = "}";
  String activeProfile;
  Module module;

  public PropertiesHandler(Module module) {
    this.module = module;
  }

  public String[] getFileExtensions() {
    return new String[] { "properties", "yml" };
  }

  public String[] getConfigFiles() {
    return new String[] { "application", "bootstrap" };
  }

  public String getServerPort() {
    String port = null;
    String serverPortKey = "server.port";

    activeProfile = findProfilePropertyValue();

    //
    if (activeProfile != null) {
      port = findPropertyValue(serverPortKey, activeProfile);
    }
    if (port == null) {
      port = findPropertyValue(serverPortKey, null);
    }

    return port != null ? port : "";
  }

  public String getProperty(String propertyKey) {
    String propertyValue = null;

    activeProfile = findProfilePropertyValue();

    //
    if (activeProfile != null) {
      propertyValue = findPropertyValue(propertyKey, activeProfile);
    }
    if (propertyValue == null) {
      propertyValue = findPropertyValue(propertyKey, null);
    }

    return propertyValue != null ? propertyValue : "";
  }

  /* try find spring.profiles.active value */
  private String findProfilePropertyValue() {
    return findPropertyValue(SPRING_PROFILE, null);
  }

  /* Disregarding the path issue for now, the first file found by default  */
  private String findPropertyValue(String propertyKey, String activeProfile) {
    String value = null;
    String profile = activeProfile != null ? "-" + activeProfile : "";
    //
    for (String conf : getConfigFiles()) {
      for (String ext : getFileExtensions()) {
        // load spring config file
        String configFile = conf + profile + "." + ext;
        if ("properties".equals(ext)) {
          Properties properties = loadProertiesFromConfigFile(configFile);
          if (properties != null) {
            Object valueObj = properties.getProperty(propertyKey);
            if (valueObj != null) {
              value = cleanPlaceholderIfExist((String) valueObj);
              return value;
            }
          }
        } else if ("yml".equals(ext) || "yaml".equals(ext)) {
          Map<String, Object> propertiesMap = getPropertiesMapFromYamlFile(configFile);
          if (propertiesMap != null) {
            Object valueObj = propertiesMap.get(propertyKey);
            if (valueObj == null) {
              return null;
            }

            if (valueObj instanceof String) {
              value = cleanPlaceholderIfExist((String) valueObj);
            } else {
              value = valueObj.toString();
            }
            return value;
          }
        }
      }
    }

    return value;
  }

  private Properties loadProertiesFromConfigFile(String configFile) {
    Properties properties = null;
    PsiFile applicationPropertiesFile = findPsiFileInModule(configFile);
    if (applicationPropertiesFile != null) {
      properties = loadPropertiesFromText(applicationPropertiesFile.getText());
    }
    return properties;
  }

  @NotNull
  private Properties loadPropertiesFromText(String text) {
    Properties prop = new Properties();
    try {
      prop.load(new StringReader(text));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return prop;
  }

  public String getContextPath() {
    String key = "server.context-path";
    String keyNext = "server.servlet.context-path";

    String contextPath = null;

    activeProfile = findProfilePropertyValue();

    if (activeProfile != null) {
      contextPath = findPropertyValue(key, activeProfile);

      //try to find key server.servlet.context-path
      if (contextPath == null) {
        contextPath = findPropertyValue(keyNext, activeProfile);
      }
    }
    if (contextPath == null) {
      contextPath = findPropertyValue(key, null);

      //try to find key server.servlet.context-path
      if (contextPath == null) {
        contextPath = findPropertyValue(keyNext, null);
      }
    }

    return contextPath != null ? contextPath : "";
  }

  private String cleanPlaceholderIfExist(String value) {
    if (
      value != null && value.contains(placeholderPrefix) && value.contains(valueSeparator)
    ) {
      String[] split = value.split(valueSeparator);
      if (split.length > 1) {
        value = split[1].replace(placeholderSuffix, "");
      }
    }
    return value;
  }

  private Map<String, Object> getPropertiesMapFromYamlFile(String configFile) {
    PsiFile applicationPropertiesFile = findPsiFileInModule(configFile);
    if (applicationPropertiesFile != null) {
      Yaml yaml = new Yaml();

      String yamlText = applicationPropertiesFile.getText();
      try {
        Map<String, Object> ymlPropertiesMap = (Map<String, Object>) yaml.load(yamlText);
        return getFlattenedMap(ymlPropertiesMap);
      } catch (Exception e) {
        // FIXME: spring When configuring multiple environments in the same file;
        //  yaml formatting is not standardized, e.g., contains "--"
        e.printStackTrace();
        return null;
      }
    }
    return null;
  }

  private PsiFile findPsiFileInModule(String fileName) {
    PsiFile psiFile = null;
    PsiFile[] applicationProperties = FilenameIndex.getFilesByName(
      module.getProject(),
      fileName,
      GlobalSearchScope.moduleScope(module)
    );

    if (applicationProperties.length > 0) {
      psiFile = applicationProperties[0];
    }

    return psiFile;
  }

  /**
   * ref: org.springframework.beans.factory.config.YamlProcessor
   */
  protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    this.buildFlattenedMap(result, source, null);
    return result;
  }

  private void buildFlattenedMap(
    Map<String, Object> result,
    Map<String, Object> source,
    String path
  ) {
    Iterator<Map.Entry<String, Object>> iterator = source.entrySet().iterator();

    while (true) {
      while (iterator.hasNext()) {
        Map.Entry<String, Object> entry = iterator.next();
        String key = entry.getKey();
        if (StringUtils.isNotBlank(path)) {
          if (key.startsWith("[")) {
            key = path + key;
          } else {
            key = path + '.' + key;
          }
        }

        Object value = entry.getValue();
        if (value instanceof String) {
          result.put(key, value);
        } else if (value instanceof Map) {
          Map<String, Object> map = (Map) value;
          this.buildFlattenedMap(result, map, key);
        } else if (value instanceof Collection) {
          Collection<Object> collection = (Collection) value;
          int count = 0;
          Iterator var10 = collection.iterator();

          while (var10.hasNext()) {
            Object object = var10.next();
            this.buildFlattenedMap(
                result,
                Collections.singletonMap("[" + count++ + "]", object),
                key
              );
          }
        } else {
          result.put(key, value != null ? value : "");
        }
      }

      return;
    }
  }
}
