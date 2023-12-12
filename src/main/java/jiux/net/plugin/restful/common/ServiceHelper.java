package jiux.net.plugin.restful.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import java.util.ArrayList;
import java.util.List;
import jiux.net.plugin.restful.common.resolver.JakartaResolver;
import jiux.net.plugin.restful.common.resolver.JaxrsResolver;
import jiux.net.plugin.restful.common.resolver.ServiceResolver;
import jiux.net.plugin.restful.common.resolver.SpringResolver;
import jiux.net.plugin.restful.navigation.action.RestServiceItem;
import jiux.net.plugin.restful.navigator.RestServiceProject;
import org.jetbrains.annotations.NotNull;

public class ServiceHelper {

  public static final Logger LOG = Logger.getInstance(ServiceHelper.class);
  PsiMethod psiMethod;

  public ServiceHelper(PsiMethod psiMethod) {
    this.psiMethod = psiMethod;
  }

  public static List<RestServiceProject> buildRestServiceProjectListUsingResolver(
    Project project
  ) {
    //        System.out.println("buildRestServiceProjectList");
    List<RestServiceProject> serviceProjectList = new ArrayList<>();

    Module[] modules = ModuleManager.getInstance(project).getModules();
    for (Module module : modules) {
      List<RestServiceItem> restServices = buildRestServiceItemListUsingResolver(module);
      if (!restServices.isEmpty()) {
        serviceProjectList.add(new RestServiceProject(module, restServices));
      }
    }

    return serviceProjectList;
  }

  public static List<RestServiceItem> buildRestServiceItemListUsingResolver(
    Module module
  ) {
    List<RestServiceItem> itemList = new ArrayList<>();

    SpringResolver springResolver = new SpringResolver(module);
    JaxrsResolver jaxrsResolver = new JaxrsResolver(module);
    JakartaResolver jakartaResolver = new JakartaResolver(module);

    ServiceResolver[] resolvers = { springResolver, jaxrsResolver, jakartaResolver };

    for (ServiceResolver resolver : resolvers) {
      List<RestServiceItem> allSupportedServiceItemsInModule =
        resolver.findAllSupportedServiceItemsInModule();

      itemList.addAll(allSupportedServiceItemsInModule);
    }

    return itemList;
  }

  @NotNull
  public static List<RestServiceItem> buildRestServiceItemListUsingResolver(
    Project project
  ) {
    List<RestServiceItem> itemList = new ArrayList<>();

    SpringResolver springResolver = new SpringResolver(project);
    JaxrsResolver jaxrsResolver = new JaxrsResolver(project);
    JakartaResolver jakartaResolver = new JakartaResolver(project);

    Module[] modules = ModuleManager.getInstance(project).getModules();

    if (modules.length > 1) {
      //multiple modules
      for (Module module : modules) {
        itemList.addAll(buildRestServiceItemListUsingResolver(module));
      }
    } else {
      ServiceResolver[] resolvers = { springResolver, jaxrsResolver, jakartaResolver };
      for (ServiceResolver resolver : resolvers) {
        List<RestServiceItem> allSupportedServiceItemsInProject =
          resolver.findAllSupportedServiceItemsInProject();

        itemList.addAll(allSupportedServiceItemsInProject);
      }
    }

    return itemList;
  }
}
