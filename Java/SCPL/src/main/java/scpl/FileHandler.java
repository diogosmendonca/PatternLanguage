package scpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class FileHandler {
	
	private static String separator = "###---###---###---###---###---###---###";

	private static Logger log = Logger.getLogger(FileHandler.class);
	
	private FileHandler() {}

	public static void browseFiles(File file, List<File> files) {
		// Testa se o arquivo é uma pasta
		if (file.isDirectory()) {
			// lista os arquivos da pasta
			String[] subDirectory = file.list();

			// Verifica se a lista de arquivos não é nula(Vazia)
			if (subDirectory != null) {
				// lista os arquivos desse subdiretório e pra cada arquivo, chama recursivamente
				// o mesmo método
				for (String dir : subDirectory) {
					browseFiles(new File(file + File.separator + dir), files);
				}
			}
			// Testa se o arquivo é um código fonte Java (termina com .java) e adiciona na
			// lista de arquivos da classe
		} else if (file.getName().endsWith(".java")) {
			files.add(file);
		}
	}

	public static File[] getFiles(String rootPath) {
		List<File> files = new ArrayList<>();
		log.info(separator);
		log.info("Buscando arquivos e pastas");
		browseFiles(new File(rootPath),files);
		log.info(separator);
		log.info("Total de arquivos: " +files.size());
		log.info("Fim da Busca de arquivos.");
		return files.isEmpty() ? null : files.toArray(new File[0]);
	} 

}
