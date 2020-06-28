/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.cefetrj.sisgee.migracao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * Migra os dados de termo e aluno do CSV gerado do Access.
 * 
 * @author c18634659798
 */
public class PrincipalTermo {
    
    public static void main(String[] args) throws ParseException {
        
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("sisgeePU");
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        
        ArrayList<TermoEst> listaTermo = new ArrayList<TermoEst>();
        ArrayList<Estudante> listaAluno = new ArrayList<Estudante>();
        String [] arrayLinha;
        
        int cont = 0;
        
        
        try{
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\c18634659798\\Documents\\Access\\Termos_de_Compromisso_sem_barra_r.csv"));
            while(br.ready()){   
                    TermoEst t= new TermoEst();
                    Estudante a = new Estudante();
                    String linha = br.readLine();
                    System.out.println("leu linha:" + linha);
                    if(cont==0){
                        cont++;
                    }
                    else{
                        cont++;
                        if(cont==219){
                            linha= linha.substring(0,33 )+linha.substring(34 );
                            arrayLinha=linha.split(";");
                        }
                        else{
                            arrayLinha=linha.split(";");
                        }
                        arrayLinha[0]=arrayLinha[0].replace('-', '/').replace(" ", ""); // Para padronizar todos os números
                        for(int i=0;i<arrayLinha[0].length()-1;i++){
                            if(arrayLinha[0].charAt(i)=='/'&& arrayLinha[0].charAt(i+1)=='/'){
                                arrayLinha[0]=arrayLinha[0].substring(0, i)+arrayLinha[0].substring(i+1);
                            }
                            if(arrayLinha[0].charAt(i)=='/'&& Character.isDigit(arrayLinha[0].charAt(i+1))==true ){
                               if(arrayLinha[0].length()-i==5) {
                                   arrayLinha[0]=arrayLinha[0].substring(0, i+1)+arrayLinha[0].substring(i+3);
                               }
                            }
                        }
                        if(arrayLinha[0].length()>1){
                            if(arrayLinha[0].charAt(0)=='0'){ // Para retirar o zero na frente dos números que não deveriam possuir esse zero.
                                if(!arrayLinha[0].trim().equals("053/14")&&!arrayLinha[0].trim().equals("002/12")&&!arrayLinha[0].trim().equals("048/17")&&!arrayLinha[0].trim().equals("069/13")&&!arrayLinha[0].trim().equals("0044/14")&&!arrayLinha[0].trim().equals("008/09")&&!arrayLinha[0].trim().equals("019/18")&&!arrayLinha[0].trim().equals("0079/12")&&!arrayLinha[0].trim().equals("001/12")&&!arrayLinha[0].trim().equals("0365/14")&&!arrayLinha[0].trim().equals("099/12")&&!arrayLinha[0].trim().equals("041/13")){
                                    arrayLinha[0]=arrayLinha[0].substring(1);
                                }
                            }
                        }
                        t.setConvenio(arrayLinha[0]);
                        if(arrayLinha[0].equals("44/14")){
                            t.setConvenio("0044/14");
                        }
                        if(arrayLinha[0].equals("CAPACITARE")){
                            t.setConvenio("5266/18");
                        }
                        if(arrayLinha[0].equals("MUDES")){
                            t.setConvenio("1721/17");
                        }
                        if(arrayLinha[0].equals("NUBE")){
                            t.setConvenio("2434/16");
                        }
                        if(arrayLinha[0].equals("CIEE")||arrayLinha[0].equals("ciee") ){
                            t.setConvenio("3067/17");
                        }
                        if(arrayLinha[1].contains("PREFEITURA MUNICIPAL DE NOVA IGUAÇU")){
                            t.setConvenio("0365/14");
                        }
                        if(arrayLinha[1].contains("MINISTÉRIO PÚBLICO DO TRABALHO")){
                            t.setConvenio("2926/17");
                        }
                        if(arrayLinha[1].contains("GENERAL ELE")){
                            t.setConvenio("4944/17");
                        }
                        if(arrayLinha[1].contains("FURNAS")){
                            t.setConvenio("1299/16");
                        }
                        if(arrayLinha[1].contains("ACCENTURE DO BRASIL")){
                            t.setConvenio("1583/18");
                        }
                        if(arrayLinha[1].contains("PROVEDOR DE TALENTOS")){
                            t.setConvenio("1903/18");
                        }
                        if(arrayLinha[1].contains("GESTÃO DE TALENTOS")){
                            t.setConvenio("1908/18");
                        }
                        if(arrayLinha[1].contains("GLOBO COMUNICAÇÃO")){
                            t.setConvenio("2693/17");
                        }
                        if(arrayLinha[1].contains("IBM BRASI")){
                            t.setConvenio("048/17");
                        }
                        /* como tem o nome diferente do número, esperar a resposta da lista
                        if(arrayLinha[1].contains("PROCURADORIA GERAL")){
                            t.setConvenio("4628/15");
                        }*/
                        if(arrayLinha[1].contains("AMC")){
                            t.setConvenio("5262/18");
                        }
                        if(arrayLinha[1].contains("GLAXOSMITHKLINE BRASIL")){
                            t.setConvenio("730/14");
                        }
                        
                        t.setNomeEmpresa(arrayLinha[1]);
                        
                        arrayLinha[1]=arrayLinha[1].replaceAll(" ", "");
                        
                        if(arrayLinha[1].contains("CET-")){
                            t.setConvenio("053/14");
                        }
                        if(arrayLinha[1].contains("LIGHT-SERVIÇOS")){
                            t.setConvenio("1887/18");
                        }
                        if(arrayLinha[0].length()>4){
                            if(arrayLinha[1].contains("PETRO")&& arrayLinha[0].substring(0, 4).equals("2076")){
                                t.setConvenio("2076/14");
                            }
                            if(arrayLinha[0].substring(0, 4).equals("3269")){
                                t.setConvenio("3269/18");
                            }
                            
                        }
                        if( arrayLinha[0].equals("40/12")){
                            t.setConvenio("40/17");
                        }
                        if( arrayLinha[0].equals("2434")){
                            t.setConvenio("2434/16");
                        }
                        if( arrayLinha[0].equals("5027")){
                            t.setConvenio("5027/17");
                        }
                        
                        if(arrayLinha[2].length()>3 && Character.isLetter(arrayLinha[2].charAt(0))==true && arrayLinha[2].charAt(0)!='x' ){ 
                            t.setEndereco(arrayLinha[2]);
                        }
                        String regex = "(\\(\\d{2}\\))? ?\\d{4,5} ? ?-? ?\\d{4}";
                        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                        String string = arrayLinha[2];
                        Matcher matcher = pattern.matcher(string);
                        while (matcher.find()) {
                            //System.out.println("Full match: " + matcher.group(0));
                            t.setTelefone(matcher.group(0));
                        }
                        if(t.getTelefone()!= null){
                            t.setTelefone(t.getTelefone().replace(":", "").replace(" ", "").replace("-", "").replace(".", "").replace("(", "").replace(")", ""));
                            if(t.getTelefone().length()<=9){
                                t.setTelefone("21"+t.getTelefone());
                            }
                        }
                        arrayLinha[2]=arrayLinha[2].replace(":", "").replace(" ", "").replace("-", "").replace(".", "").replace("(", "").replace(")", "");
                        
                        
                        
                        
                       /* for (int i=0;i<arrayLinha[2].length()-10;i++ ){
                            if(arrayLinha[2].substring(i,i+7).equalsIgnoreCase("CIDADE")){
                                t.setCidade(arrayLinha[2].substring(i+7,i+11));
                            }
                        }*/
                        for (int i=0;i<arrayLinha[2].length()-10;i++ ){
                            if(arrayLinha[2].substring(i,i+3).equalsIgnoreCase("CEP")||arrayLinha[2].substring(i,i+3).equalsIgnoreCase("cep") ){
                                t.setCep(arrayLinha[2].substring(i+3,i+11));
                                arrayLinha[2]=arrayLinha[2].substring(0,i)+arrayLinha[2].substring(i+11);
                            }
                        }
                        PrincipalTermo p= new PrincipalTermo();
                     //    System.out.println("Foi end ");
                        for (int i=0;i<arrayLinha[3].length();i++ ){
                            if(cont==1722 || cont == 7282 || cont == 18444){
                                break;
                            }
                            if(arrayLinha[3].charAt(i)==' ' ){
                                if(i+2<arrayLinha[3].length()){
                                    if(arrayLinha[3].charAt(i+2)!=' '){
                                        if(i!=arrayLinha[3].length()/2){                                    
                                            arrayLinha[3]=arrayLinha[3].replace(" ", "");
                                        }
                                    }else{
                                        arrayLinha[3]=arrayLinha[3].substring(0, 6)+arrayLinha[3].substring(8);
                                    }
                                }
                            }
                            if(arrayLinha[3].length()>12){ 
                                if((arrayLinha[3].charAt(i)=='A' || arrayLinha[3].charAt(i)=='N' || arrayLinha[3].charAt(i)=='Q' || arrayLinha[3].charAt(i)=='Â' || arrayLinha[3].charAt(i)=='a' || arrayLinha[3].charAt(i)=='À' || arrayLinha[3].charAt(i)=='à' || arrayLinha[3].charAt(i)==' ' || arrayLinha[3].charAt(i)=='Á' || arrayLinha[3].charAt(i)=='á' || arrayLinha[3].charAt(i)=='S') ){
                                    if(arrayLinha[3].length()>15){
                                        if(cont==183){
                                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                            Date data = formato.parse(arrayLinha[3].substring(0, 2)+"/"+arrayLinha[3].substring(2, 4)+"/"+"2010");
                                            t.setDataInicio(data);
                                        }else{
                                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                            Date data = formato.parse(arrayLinha[3].substring(0, 2)+"/"+arrayLinha[3].substring(2, 4)+"/"+arrayLinha[3].substring(4, i));
                                            t.setDataInicio(data);
                                        }
                                        if(cont==5650){
                                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                            t.setDataFim(formato.parse("15/07/2008"));
                                        }else{
                                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                            Date data= formato.parse(arrayLinha[3].substring(i+1, i+3)+"/"+arrayLinha[3].substring(i+3, i+5)+"/"+arrayLinha[3].substring(i+5));
                                            t.setDataFim(data);
                                            
                                            if(cont==713){
                                                t.setDataInicio(formato.parse("21/02/2002"));
                                            }
                                        }
                                        //t=p.confere_data(t);
                                    }else{
                                        if(arrayLinha[3].length()==15){
                                            if(i>arrayLinha[3].length()/2){
                                                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                                Date data = formato.parse(arrayLinha[3].substring(0, 2)+"/"+arrayLinha[3].substring(2, 4)+"/"+arrayLinha[3].substring(4, i));
                                                t.setDataInicio(data);
                                                formato = new SimpleDateFormat("dd/MM/yy");
                                                data= formato.parse(arrayLinha[3].substring(i+1, i+3)+"/"+arrayLinha[3].substring(i+3, i+5)+"/"+arrayLinha[3].substring(i+5));
                                                t.setDataFim(data);
                                            }
                                            else{
                                                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy");
                                                Date data = formato.parse(arrayLinha[3].substring(0, 2)+"/"+arrayLinha[3].substring(2, 4)+"/"+arrayLinha[3].substring(4, 6));
                                                t.setDataInicio(data);
                                                formato = new SimpleDateFormat("dd/MM/yyyy");
                                                data= formato.parse(arrayLinha[3].substring(i+1, i+3)+"/"+arrayLinha[3].substring(i+3, i+5)+"/"+arrayLinha[3].substring(i+5));
                                                t.setDataFim(data);
                                            }
                                        }else{
                                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy");
                                            Date data = formato.parse(arrayLinha[3].substring(0, 2)+"/"+arrayLinha[3].substring(2, 4)+"/"+arrayLinha[3].substring(4, 6));
                                            t.setDataInicio(data);
                                            data= formato.parse(arrayLinha[3].substring(i+1, i+3)+"/"+arrayLinha[3].substring(i+3, i+5)+"/"+arrayLinha[3].substring(i+5));
                                            t.setDataFim(data);
                                            
                                        }
                                        //t=p.confere_data(t);
                                    }
                                    
                                }
                                
                            }
                            
                            
                        }
                       /* System.out.println("AQui");
                        if(t.getDataInicio()!=null && t.getDataFim()!=null){
                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy");
                            Date data = formato.parse("01/01/2022");

                            if(t.getDataFim().after(data)){
                                t.setDataFim(null);
                            }
                            data = formato.parse("01/01/1995");
                            if(t.getDataInicio().before(data)){
                                t.setDataInicio(null);
                            }
                            if(t.getDataFim().before(data)){
                                t.setDataFim(null);
                            }
                            data = formato.parse("10/07/2018");
                            if(t.getDataInicio().after(data)){
                                t.setDataInicio(null);
                            }
                        }
                        System.out.println("passou");*/
                        if(arrayLinha[4].trim().charAt(0)=='X' && arrayLinha[4].charAt(1)=='X' ){  
                        }
                        else{
                            a.setNome(arrayLinha[4]);
                        }
                   
                        arrayLinha[5]=arrayLinha[5].replace(":", "").replace(" ", "").replace("-", "").replace(".", "");
                        if(arrayLinha[5].length()>8){
                            if(Character.isDigit(arrayLinha[5].charAt(0))==true && Character.isDigit(arrayLinha[5].charAt(1))==true){
                                a.setIdentidade(arrayLinha[5]);
                            }
                        }
                        a.setUnidade(arrayLinha[6]);
                   
                        if(arrayLinha[7].length()>2){
                            if(Character.isDigit(arrayLinha[7].charAt(2))==false){
                                a.setCurso(arrayLinha[7]);
                            }
                        }
                        if(arrayLinha[8].length()>0){
                            if(arrayLinha[8].charAt(0)!='*' && arrayLinha[8].charAt(0)!='-'  ){
                                 a.setSerie(arrayLinha[8]);
                             } 
                            if(Character.isDigit(arrayLinha[8].charAt(0))==false){
                                if(arrayLinha[8].substring(0, 1).equalsIgnoreCase("'")){
                                    a.setSerie("");
                                }
                            }
                        }
                        if(arrayLinha[9].length()>3 && Character.isLetter(arrayLinha[9].charAt(0))==true && arrayLinha[9].charAt(0)!='x' && arrayLinha[9].charAt(0)!='X' ){ 
                            a.setEndereco(arrayLinha[9]);
                        }
                        regex = "(\\(\\d{2}\\))? ?\\d{4,5} ? ?-? ?\\d{4}";
                        pattern = Pattern.compile(regex, Pattern.MULTILINE);
                        string = arrayLinha[9];
                        matcher = pattern.matcher(string);
                        while (matcher.find()) {
                            a.setTelefone(matcher.group(0));
                        }
                        if(a.getTelefone()!= null){
                            a.setTelefone(a.getTelefone().replace(":", "").replace(" ", "").replace("-", "").replace(".", "").replace("(", "").replace(")", ""));
                            if(a.getTelefone().length()<=9){
                                a.setTelefone("21"+a.getTelefone());
                            }
                        }
                        arrayLinha[9]=arrayLinha[9].replace(":", "").replace(" ", "").replace("-", "").replace(".", "").replace("(", "").replace(")", "");
                        
                        for (int i=0;i<arrayLinha[9].length()-11;i++ ){
                            if(arrayLinha[9].substring(i,i+3).equalsIgnoreCase("CEP")||arrayLinha[9].substring(i,i+3).equalsIgnoreCase("cep") ){
                                a.setCep(arrayLinha[9].substring(i+3,i+11));
                                arrayLinha[9]=arrayLinha[9].substring(0,i)+arrayLinha[9].substring(i+11);
                            }
                        }
                        if(arrayLinha[9].length()<=14){
                            for (int i=0;i<arrayLinha[9].length()-3;i++ ){
                            if(arrayLinha[9].substring(i,i+3).equalsIgnoreCase("CPF")||arrayLinha[9].substring(i,i+3).equalsIgnoreCase("cpf") ){
                                a.setIdentidade(arrayLinha[9].substring(i+3));
                                a.setEndereco("");
                            }
                        }
                        }
                        for (int i=0;i<arrayLinha[9].length()-13;i++ ){
                            if(cont == 14 && i+1== arrayLinha[9].length()-13){  
                                
                            }
                            if(arrayLinha[9].substring(i,i+3).equalsIgnoreCase("CPF")||arrayLinha[9].substring(i,i+3).equalsIgnoreCase("cpf") ){
                                a.setIdentidade(arrayLinha[9].substring(i+3,i+14));
                                arrayLinha[9]=arrayLinha[9].substring(0,i)+arrayLinha[9].substring(i+14);
                            }
                        }
                        for (int i=0;i<arrayLinha[9].length()-4;i++ ){
                            if(arrayLinha[9].substring(i,i+2).equalsIgnoreCase("UF")||arrayLinha[9].substring(i,i+3).equalsIgnoreCase("uf") ){
                                a.setEstado(arrayLinha[9].substring(i+2,i+4));
                                arrayLinha[9]=arrayLinha[9].substring(0,i)+arrayLinha[9].substring(i+4);
                            }
                        }
                       
                       // t.setBairro(arrayLinha[2]);
                        //t.setCep(arrayLinha[3]);
                        t.setCartaRecisao(arrayLinha[10]);
                        t.setTermoAditivo(arrayLinha[11]);
                        if( arrayLinha.length>12){  
                            if(arrayLinha[12].length()>0){
                                if(Character.isDigit(arrayLinha[12].charAt(0))==true){
                                    t.setCargaHoraria(Integer.parseInt(arrayLinha[12])); 
                                }
                            }
                        }
                        if(arrayLinha.length>13){
                            /*  TermoEst t2= new TermoEst();
                            t2.setConvenio(arrayLinha[15]);
                            t2.setNomeEmpresa(arrayLinha[16]);
                            t2.setEndereco(arrayLinha[17]);
                            t2.setCartaRecisao(arrayLinha[19]);
                            t2.setTermoAditivo(arrayLinha[20]);
                           // t2.setCargaHoraria(arrayLinha[21]);
                           // t2.setValorBolsa(arrayLinha[22]);
                            */
                            if(arrayLinha[13].length()>0){
                                if(Character.isDigit(arrayLinha[13].charAt(0))==true){
                                    t.setValorBolsa(Float.parseFloat(arrayLinha[13].replace(",", ".")));
                                }
                            }
              // MODIFICAÇÕES AQUI              SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                            /*
                            //tentativa de migração dos termos aditivos.
                            Date data = formato.parse("31/08/2018");
                            if(arrayLinha.length> 15 &&  t.getDataFim().after(data) ){
                                if(!arrayLinha[10].contains("N") && arrayLinha[15].trim().equals(t.getConvenio()) ){
                                    TermoAditivoAux tA = new TermoAditivoAux();
                                }
                                
                            }
                            */
                            
                        }
                        // Convenio c = new Convenio();
                        t=p.confere_data(t);
                        t.setAluno(a);
                        //t.setC(c);
                        manager.persist(t);   
                        listaTermo.add(t);
                        manager.persist(a);
                        listaAluno.add(a);
                        }
            }
            br.close();
            manager.getTransaction().commit();
            
        }catch(Exception ioe){
            System.out.println("na linha " + cont);
            ioe.printStackTrace();
            manager.getTransaction().rollback();
            System.exit(-1);
        }finally{
            System.out.println("numero de linhas " + cont);
            manager.close();
            factory.close();
        }  
        
    }
    public TermoEst confere_data(TermoEst t) throws ParseException{
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date data = formato.parse("01/01/2022");
        if(t.getDataFim()!= null){
            if(t.getDataFim().after(data)){
                t.setDataFim(null);
            }
        }
        data = formato.parse("01/01/1995");
        if(t.getDataInicio()!= null){
            if(t.getDataInicio().before(data)){
                t.setDataInicio(null);

            }
        }
        if(t.getDataFim()!= null){
            if(t.getDataFim().before(data)){
                t.setDataFim(null);
            }
        }
        data = formato.parse("10/07/2018");
        if(t.getDataInicio()!= null){
            if(t.getDataInicio().after(data)){
                t.setDataInicio(null);
            }
        }
        data = formato.parse("03/08/2018");
        if(t.getDataFim()!= null){
            if(t.getDataInicio()== null && t.getDataFim().compareTo(data)==0){
                System.out.println("Entrou");
                t.setDataFim(null);
            }
        }
        return t;
    }
}
