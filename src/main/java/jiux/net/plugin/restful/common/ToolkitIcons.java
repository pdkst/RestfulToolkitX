package jiux.net.plugin.restful.common;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import javax.swing.*;
import jiux.net.plugin.restful.method.HttpMethod;

public class ToolkitIcons {

  public static final Icon MODULE = AllIcons.Modules.UnloadedModule; // 16x16
  public static final Icon Refresh = AllIcons.Actions.Refresh; // 16x16
  //    public static final Icon SERVICE = IconLoader.getIcon("/icons/s1.png"); // 16x16
  //    public static final Icon SERVICE = IconLoader.getIcon("/icons/s2.png"); // 16x16
  //    public static final Icon SERVICE = IconLoader.getIcon("/icons/s3.png"); // 16x16

  public static final Icon SERVICE = IconLoader.getIcon(
    "/icons/service.png",
    ToolkitIcons.class
  ); // 16x16

  //    public static final Icon SERVICE = IconLoader.getIcon("/icons/service.png"); // 16x16

  public static class METHOD {

    public static Icon GET = IconLoader.getIcon(
      "/icons/method/g.png",
      ToolkitIcons.class
    ); // 16x16 GREEN
    // post put patch
    public static Icon PUT = IconLoader.getIcon(
      "/icons/method/p2.png",
      ToolkitIcons.class
    ); // 16x16 ORANGE
    public static Icon POST = IconLoader.getIcon(
      "/icons/method/p.png",
      ToolkitIcons.class
    ); // 16x16 BLUE
    public static Icon PATCH = IconLoader.getIcon(
      "/icons/method/p3.png",
      ToolkitIcons.class
    ); // 16x16 GRAY
    public static Icon DELETE = IconLoader.getIcon(
      "/icons/method/d.png",
      ToolkitIcons.class
    ); // 16x16 RED
    public static Icon UNDEFINED = IconLoader.getIcon(
      "/icons/method/undefined.png",
      ToolkitIcons.class
    ); // 16x16 GRAY

    public static Icon get(HttpMethod method) {
      if (method == null) {
        return UNDEFINED;
      }
      if (method.equals(HttpMethod.GET)) {
        return METHOD.GET;
      } else if (method.equals(HttpMethod.POST)) {
        return METHOD.POST;
      } else if (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PATCH)) {
        return METHOD.PUT;
      } else if (method.equals(HttpMethod.DELETE)) {
        return METHOD.DELETE;
      }
      return null;
    }
    // OPTIONS HEAD
  }
}
