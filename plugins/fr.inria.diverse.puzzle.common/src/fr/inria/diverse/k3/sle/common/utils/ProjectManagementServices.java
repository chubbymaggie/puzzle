package fr.inria.diverse.k3.sle.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.jdt.core.JavaCore;

import fr.inria.diverse.k3.sle.common.vos.AspectLanguageMapping;
import fr.inria.diverse.melange.metamodel.melange.Aspect;

/**
 * Class that provides services for projects management in a given eclipse workspace.
 * @author David Mendez-Acuna
 */
public class ProjectManagementServices {

	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------
	
	public static final String XTEND_NATURE_ID = "org.eclipse.xtext.ui.shared.xtextNature";
	public static final String PLUGIN_NATURE_ID = "org.eclipse.pde.PluginNature";
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	/**
	 * Creates an eclipse project in the workspace
	 * @param projectName
	 * @throws CoreException
	 */
	public static IProject createEclipseJavaProject(String projectName)
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
	 * Creates an eclipse java project in the workspace
	 * @param projectName
	 * @throws CoreException
	 */
	public static IProject createEclipseEmptyProject(String projectName)
			throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);

		IProjectDescription description = project.getDescription();
		project.setDescription(description, null);

		return project;
	}
	
	/**
	 * Creates an Xtend project in the workspace
	 * @param projectName
	 * @throws CoreException
	 */
	public static IProject createEclipseXtendProject(String projectName)
			throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);

		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		description.setNatureIds(new String[] { XTEND_NATURE_ID });
		description.setNatureIds(new String[] { PLUGIN_NATURE_ID });
		project.setDescription(description, null);

		return project;
	}
	
	/**
	 * Returns an eclipse project in the workspace by its name. Returns null if the project does not exist.
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
	 * Refreshes the given project. 
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

	/**
	 * Returns a file with the given name in the given project. 
	 * @param project
	 * @param fileName
	 * @return
	 */
	public static File getFile(IProject project, final String fileName) {
		if(project != null){
			File projectFile = new File(project.getLocation().toString());
			return findFile(projectFile, fileName);
		}
		return null;
	}
	
	/**
	 * Returns a file with the given name by searching in the files hierarchy starting in the given file.
	 * @param root
	 * @param fileName
	 * @return
	 */
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
	 * Copy an aspect to the modularized semantic module. 
	 * @param aspectLanguageMapping
	 * @param moduleProject
	 * @param moduleName
	 * @param classifiers
	 * @param requiredAspects
	 * @throws IOException
	 */
	public static void copyAspectResource(AspectLanguageMapping aspectLanguageMapping,
			IProject moduleProject, String moduleName, ArrayList<EClassifier> classifiers, 
			ArrayList<Aspect> requiredAspects) throws IOException {
		
		Resource eResource = aspectLanguageMapping.getAspect().getAspectTypeRef().getType().eResource();
		URI fileURIResource0 = URI.createFileURI(eResource.getURI().path());
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(fileURIResource0.segments()[1]);
		
		String fileName = fileURIResource0.lastSegment();
		File xtendFile = getFile(project, fileName);
		
		BufferedReader br = new BufferedReader(new FileReader(xtendFile));
		String line = br.readLine();
		String content = "";
		
		String pck = moduleName.replace("Module", "").toLowerCase();
		String prefix = aspectLanguageMapping.getLanguagesList();// + "Like";
		
		while(line != null){
			if(line.startsWith("package")){
				line = "package " + prefix + "." + pck + "\n\n";
				line += "import commons.*\n";
			}
			
			if(line.startsWith("import") && !line.contains("static extension")){
				for (EClassifier eClassifier : classifiers) {
					if(line.endsWith("." + eClassifier.getName())){
						line = "import " + moduleName + "." + eClassifier.getName() + "\n";
					}
				}
			}
			
			else if(line.startsWith("import") && line.contains("static extension")){
				boolean include = true;
				for (Aspect aspect : requiredAspects) {
					if(line.endsWith(aspect.getAspectTypeRef().getType().getSimpleName() + ".*")){
						include = false;
						break;
					}
				}
				if(include)
					line = line.replaceAll("import static extension \\w+.", "import static extension " + pck +".");
				else{
					line = "";
				}
			}
			
			if(!line.equals(""))
				content += line + "\n";
			
			line = br.readLine();
		}
		br.close();
		
		
		File pckRootFolder = new File(moduleProject.getLocation().toString() + "/src/" + prefix);
		pckRootFolder.mkdirs();
		
		File pckFolder = new File(moduleProject.getLocation().toString() + "/src/" + prefix + "/" + pck);
		pckFolder.mkdirs();
		
		File newXtendFile = new File(moduleProject.getLocation().toString() + "/src/" + prefix + "/" + pck + "/" + fileName);
		newXtendFile.createNewFile();
		PrintWriter pw = new PrintWriter(newXtendFile);
		pw.print(content);
		pw.close();
	}
	
	/**
	 * Copy a non aspected resource to the commons semantics project in the parameter. 
	 * @param file
	 * @param commonsProject
	 * @throws Exception 
	 */
	public static void copyNonAspectResource(File file, IProject commonsProject) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		String content = "";
		
		while(line != null){
			if(line.startsWith("package")){
				line = "package commons";
			}
			
			content += line + "\n";
			line = br.readLine();
		}
		br.close();
		
		File newXtendFile = new File(commonsProject.getLocation().toString() + "/src/commons" + "/" + file.getName());
		newXtendFile.createNewFile();
		PrintWriter pw = new PrintWriter(newXtendFile);
		pw.print(content);
		pw.close();
	}
	
	/**
	 * Returns a collection with all the xtend files existing in the given project.
	 * @param project
	 * @return
	 */
	public static ArrayList<File> collectAllXtendFiles(IProject project) {
		 ArrayList<File> xtendFiles = new ArrayList<File>();
		if(project != null){
			File projectFile = new File(project.getLocation().toString());
			collectAllXtendFiles(projectFile, xtendFiles);
		}
		return xtendFiles;
	}
	
	/**
	 * Returns a collection with all the xtend files existing in the hierarchy starting by the given file. 
	 * @param root
	 * @return
	 */
	private static void collectAllXtendFiles(File root, ArrayList<File> xtendFiles ) {
		File[] files = root.listFiles();
		for (int i = 0; i < root.listFiles().length; i++) {
			if(!files[i].isDirectory() && files[i].getName().endsWith(".xtend"))
				xtendFiles.add(files[i]);
		}
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory() && !files[i].getName().equals("bin") && !files[i].getName().equals(".settings")){
				collectAllXtendFiles(files[i], xtendFiles);
			}
		}
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
        genModel.setModelPluginID(projectName);
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
	
	public static void createXtendConfigurationFile(IProject project, String moduleName, boolean commons, String exportedPackage) throws IOException{
		createPluginFile(project);
		createPropertiesFile(project);
		createFolderByName(project, "src");
		createFolderByName(project, "xtend-gen");
		createFolderByName(project, "META-INF");
		createManifest(project, commons, exportedPackage);
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
	
	private static void createManifest(IProject project, boolean commons, String exportedPackage) throws IOException{
		File file = new File(project.getLocation().toString() + "/META-INF/MANIFEST.MF");
		file.createNewFile();
		String content = "Bundle-SymbolicName: " + project.getName() + ";singleton:=true\n";
		
		if(commons)
			content += "Export-Package: commons\n";
		else
			content += "Export-Package: " +  exportedPackage + "\n";
		
		content += "Bundle-Version: 1.0.0\n";
		content += "Bundle-Name: " + project.getName() + "\n";
		content += "Bundle-ClassPath: .\n";
		content += "Require-Bundle: fr.inria.diverse.k3.al.annotationprocessor.plugin;bundle-version=\"3.0.0\",\n";
		content += " org.eclipse.xtend.lib;bundle-version=\"2.6.0\";visibility:=private,\n";
		content += " org.eclipse.xtext.xbase.lib;bundle-version=\"2.6.0\";visibility:=private,\n";
		content += " com.google.guava;bundle-version=\"0.0.0\";visibility:=private,\n";
		content += " org.eclipse.emf.ecore.xmi;bundle-version=\"2.8.0\";visibility:=reexport,\n";
		content += " org.eclipse.emf.ecore;bundle-version=\"2.8.0\";visibility:=reexport,\n";
		
		if(!commons)
			content += " fr.inria.diverse.commons.semantics;visibility:=reexport,\n";
		
		if(!commons)
			content += " " + project.getName().replace("semantics", "syntax") + ";bundle-version=\"1.0.0\",\n";
		
		content += " org.eclipse.emf.common;bundle-version=\"2.8.0\";visibility:=reexport\n";
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