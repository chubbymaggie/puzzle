package fr.inria.diverse.k3.sle.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author David Mendez Acuna
 */
public class ProjectManagementServices {

	/**
	 * Creates an eclipse project in the workspace
	 * 
	 * @param projectName
	 * @throws CoreException
	 */
	public static IProject createEclipseProject(String projectName)
			throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);

		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);

		return project;
	}
	
	/**
	 * Returns an eclipse project in the workspace by its name. Returns null if the project does not exist.
	 * 
	 * @param projectName
	 * @throws CoreException
	 */
	public static IProject getEclipseProject(String projectName)
			throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		return project;
	}

	/**
	 * Creates a folder with the name in the parameter on the target project.
	 * 
	 * @param targetProject
	 * @param folderName
	 * @return
	 */
	public static String createFolderByName(IProject targetProject,
			String folderName) {
		String path = targetProject.getLocation().toString() + "/" + folderName;
		File dir = new File(path);
		dir.mkdir();

		return path;
	}

	/**
	 * 
	 * @param project
	 * @throws CoreException
	 */
	public static void refreshProject(IProject project) throws CoreException {
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	/**
	 * Copy the src folder in the dest one
	 * Taken from: http://www.mkyong.com/java/how-to-copy-directory-in-java/
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}

	public static File getFile(IProject project, final String fileName) {
		if(project != null){
			File projectFile = new File(project.getLocation().toString());
			return findFile(projectFile, fileName);
		}
		return null;
	}
	
	private static File findFile(File root, String fileName){
		File[] files = root.listFiles();
		for (int i = 0; i < root.listFiles().length; i++) {
			if(!files[i].isDirectory() && files[i].getName().equals(fileName))
				return files[i];
		}
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory() && !files[i].getName().equals("bin") && !files[i].getName().equals(".settings")){
				File found = findFile(files[i], fileName);
				if(found != null)
					return found;
			}
		}
		return null;
	}
	
	/**
	 * Generates the corresponding GenModel file for an ecore package in the parameter
	 * @param ePackage
	 * @throws IOException 
	 * @throws CoreException 
	 */
	public static void generateGenmodelFile(IProject project, EPackage rootPackage, String ecoreLocation, String genModelLocation, String projectName, String languageName) throws IOException, CoreException {
		GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
		genModel.setComplianceLevel(GenJDKLevel.JDK80_LITERAL);
		genModel.setEditDirectory("/" + projectName + ".edit/src");
		genModel.setEditPluginID(projectName + ".edit");
		genModel.setEditDirectory("/" + projectName + ".editor/src");
		genModel.setEditPluginID(projectName + ".editor");
        genModel.setModelDirectory("/" + projectName + "/src");
        genModel.setModelPluginID(projectName + ".tests");
        genModel.setOperationReflection(true);
        genModel.setTestsDirectory("/" + projectName + ".tests/src");
        genModel.setTestsPluginID(projectName);
        genModel.getForeignModel().add(new Path(ecoreLocation).lastSegment());
        genModel.setModelName(languageName);
        genModel.setRootExtendsInterface("org.eclipse.emf.ecore.EObject");
        genModel.initialize(Collections.singleton(rootPackage));
        genModel.setCanGenerate(true);
        
        GenPackage genPackage = (GenPackage)genModel.getGenPackages().get(0);
        genPackage.setPrefix(rootPackage.getNsPrefix());

        URI genModelURI = URI.createFileURI(genModelLocation);
        final XMIResourceImpl genModelResource = new XMIResourceImpl(genModelURI);
        genModelResource.getContents().add(genModel);
        genModelResource.save(Collections.EMPTY_MAP);
        
        genModel.reconcile();
    	genModel.setCanGenerate(true);
    	genModel.setValidateModel(true);

	    createJavaPluginClasspath(project);
	    refreshProject(project);
	    
	    Generator generator = new Generator();
	    generator.setInput(genModel);
	    generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, "model project", new BasicMonitor.Printing(System.out));
	}
	
	public static void createXtendConfigurationFile(IProject project, String moduleName) throws IOException{
		createPluginFile(project);
		createPropertiesFile(project);
		createFolderByName(project, "src");
		createFolderByName(project, "xtend-gen");
		createFolderByName(project, "META-INF");
		createFolderByName(project, "src/" + moduleName.replace("Module", "").toLowerCase());
		createManifest(project);
		createDotProject(project);
		createClasspath(project);
	}
	
	private static void createPluginFile(IProject project) throws IOException{
		File pluginFile = new File(project.getLocation().toString() + "/plugin.xml");
		pluginFile.createNewFile();
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		content += "<?eclipse version=\"3.4\"?>\n";
		content += "<plugin>\n";
		content += "</plugin>\n";
		PrintWriter pw = new PrintWriter(pluginFile);
		pw.print(content);
		pw.close();
	}
	
	private static void createPropertiesFile(IProject project) throws IOException{
		File file = new File(project.getLocation().toString() + "/build.properties");
		file.createNewFile();
		String content = "source.. = src/,\\\n";
		content += "           xtend-gen/\n";
		content += "output.. = bin/\n";
		content += "bin.includes = plugin.xml,\\\n";
		content += "               META-INF/,\\\n";
		content += "               .,\\\n";
		content += "               lib/,\\\n";
		content += "src.includes = lib/\n";
		content += "jars.compile.order = .\n";
		PrintWriter pw = new PrintWriter(file);
		pw.print(content);
		pw.close();
	}
	
	private static void createManifest(IProject project) throws IOException{
		File file = new File(project.getLocation().toString() + "/META-INF/MANIFEST.MF");
		file.createNewFile();
		String content = "Bundle-SymbolicName: " + project.getName() + ";singleton:=true\n";
		content += "Export-Package: aspects\n";
		content += "Bundle-Version: 1.0.0\n";
		content += "Bundle-Name: " + project.getName() + "\n";
		content += "Bundle-ClassPath: .\n";
		content += "Require-Bundle: fr.inria.diverse.k3.al.annotationprocessor.plugin;bundle-version=\"3.0.0\",\n";
		content += " org.eclipse.xtend.lib;bundle-version=\"2.6.0\";visibility:=private,\n";
		content += " org.eclipse.xtext.xbase.lib;bundle-version=\"2.6.0\";visibility:=private,\n";
		content += " com.google.guava;bundle-version=\"0.0.0\";visibility:=private,\n";
		content += " org.eclipse.emf.ecore.xmi;bundle-version=\"2.8.0\";visibility:=reexport,\n";
		content += " org.eclipse.emf.ecore;bundle-version=\"2.8.0\";visibility:=reexport,\n";
		content += " org.eclipse.emf.common;bundle-version=\"2.8.0\";visibility:=reexport,\n";
		content += " " + project.getName().replace("semantics", "syntax") + ";bundle-version=\"1.0.0\"\n";
		content += "Bundle-ManifestVersion: 2\n";
		content += "Bundle-RequiredExecutionEnvironment: JavaSE-1.7\n";
		content += "Manifest-Version: 1.0\n";
		PrintWriter pw = new PrintWriter(file);
		pw.print(content);
		pw.close();
	}
	
	private static void createDotProject(IProject project) throws IOException{
		File file = new File(project.getLocation().toString() + "/.project");
		file.createNewFile();
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		content += "<projectDescription>\n";
		content += "	<name>" + project.getName() + "</name>\n";
		content += "	<comment></comment>\n";
		content += "	<projects></projects>\n";
		content += "	<buildSpec>\n";
		content += "		<buildCommand>\n";
		content += "			<name>org.eclipse.jdt.core.javabuilder</name>\n";
		content += "			<arguments></arguments>\n";
		content += "		</buildCommand>\n";
		content += "		<buildCommand>\n";
		content += "			<name>org.eclipse.xtext.ui.shared.xtextBuilder</name>\n";
		content += "			<arguments></arguments>\n";
		content += "		<buildCommand>\n";
		content += "		<buildCommand>\n";
		content += "			<name>fr.inria.diverse.k3.ui.k3Builder</name>\n";
		content += "			<arguments></arguments>\n";
		content += "		<buildCommand>\n";
		content += "		<buildCommand>\n";
		content += "			<name>org.eclipse.pde.ManifestBuilder</name>\n";
		content += "			<arguments></arguments>\n";
		content += "		<buildCommand>\n";
		content += "		<buildCommand>\n";
		content += "			<name>org.eclipse.pde.SchemaBuilder</name>\n";
		content += "			<arguments></arguments>\n";
		content += "		<buildCommand>\n";
		content += "	</buildSpec>\n";
		content += "	<natures>\n";
		content += "		<nature>fr.inria.diverse.k3.ui.k3Nature</nature>\n";
		content += "		<nature>org.eclipse.jdt.core.javanature</nature>\n";
		content += "		<nature>org.eclipse.xtext.ui.shared.xtextNature</nature>\n";
		content += "        <nature>org.eclipse.pde.PluginNature</nature>\n";
		content += "	</natures>\n";
		content += "</projectDescription>\n";
		PrintWriter pw = new PrintWriter(file);
		pw.print(content);
		pw.close();
	}
	
	private static void createClasspath(IProject project) throws IOException{
		File file = new File(project.getLocation().toString() + "/.classpath");
		file.createNewFile();
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		content += "<classpath>\n";
		content += "	<classpathentry kind=\"src\" path=\"xtend-gen\"/>\n";
		content += "	<classpathentry kind=\"src\" path=\"src\"/>\n";
		content += "	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n";
		content += "	<classpathentry kind=\"con\" path=\"org.eclipse.pde.core.requiredPlugins\"/>\n";
		content += "	<classpathentry kind=\"output\" path=\"bin\"/>\n";
		content += "</classpath>\n";
		PrintWriter pw = new PrintWriter(file);
		pw.print(content);
		pw.close();
	}
	
	private static void createJavaPluginClasspath(IProject project) throws IOException{
		File file = new File(project.getLocation().toString() + "/.classpath");
		file.createNewFile();
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		content += "<classpath>\n";
		content += "	<classpathentry kind=\"src\" path=\"src\"/>\n";
		content += "	<classpathentry kind=\"con\" path=\"org.eclipse.pde.core.requiredPlugins\"/>\n";
		content += "	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n";
		content += "	<classpathentry kind=\"output\" path=\"bin\"/>\n";
		content += "</classpath>\n";
		PrintWriter pw = new PrintWriter(file);
		pw.print(content);
		pw.close();
	}
}