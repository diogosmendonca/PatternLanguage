package br.cefetrj.sisgee.view.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe criada para métodos de validação, para melhor reuso de código.
 *
 * @author Lucas Lima
 * @since 1.0
 */
public class ValidaUtils {

    /**
     * Método para validar campo por tamanho, para valores de texto(String)
     *
     * @param nomeCampo texto com o nome do campo.
     * @param tamanho tamanho do campo.
     * @param param valor do texto a ser testado.
     * @return String com mensagem de erro ou vazia
     */
    public static String validaTamanho(String nomeCampo, int tamanho, String param) {
        String msg = "";
        if (param != null && param.length() > tamanho) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_tamanho_txt";
        }
        return msg;
    }

    /**
     * Método para validar email se contém o @ e o .
     *
     * @param nomeCampo nome do campo a validar
     * @param param valor que será validado
     * @return msg de erro
     */
    public static String validaEmail(String nomeCampo, String param) {
        String msg = "";
        if (!param.contains("@") || !param.contains(".")) {
            msg = "br.cefetrj.sisgee.form_termo_estagio_servlet.valor_invalido";
        }
        return msg;
    }

    /**
     * Método para validar telefone com no minimo 10 de tamanho
     *
     * @param nomeCampo nome do campo a validar
     * @param param valor que será validado
     * @return msg de erro
     */
    public static String validaTelefone(String nomeCampo, String param) {
        String msg = "";
        if (param.length() < 10) {
            msg = "br.cefetrj.sisgee.form_termo_estagio_servlet.valor_invalido";
        }
        return msg;
    }

    /**
     * Valida se parametro é somente letras
     *
     * @param nomeCampo
     * @param param
     * @return
     */
    public static String validaSomenteLetras(String nomeCampo, String param) {
        String msg = "";
        if (!(param.matches("^[a-zA-ZÁÂÃÀÇÉÊÍÓÔÕÚÜáâãàçéêíóôõúü ]*$"))) {
            msg = "br.cefetrj.sisgee.form_termo_estagio_servlet.valor_invalido";
        }
        return msg;
    }

    /**
     * Método para validar campo por tamanho, para valores inteiros
     *
     * @param nomeCampo texto com o nome do campo.
     * @param tamanho tamanho do campo.
     * @param param valor inteiro a ser testado.
     * @return String com mensagem de erro ou vazia
     */
    public static String validaTamanho(String nomeCampo, int tamanho, Integer param) {
        String msg = "";
        if (param > tamanho) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_tamanho_num";
        }
        return msg;
    }

    /**
     * Método para validar campo com tamanho restito, para valores de
     * texto(String)
     *
     * @param nomeCampo texto com o nome do campo.
     * @param tamanho tamanho do campo.
     * @param param valor do texto a ser testado.
     * @return String com mensagem de erro ou vazia
     */
    public static String validaTamanhoExato(String nomeCampo, int tamanho, String param) {
        String msg = "";
        if (param.length() != tamanho) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_tamanho_exato";
        }
        return msg;
    }

    /**
     * Método para validar campo obrigatório
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param valor a ser testado como obrigatório.
     * @return String com mensagem de erro ou vazia
     */
    public static String validaObrigatorio(String nomeCampo, String param) {
        String msg = "";
        if (param == null || param.trim().isEmpty()) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_obrigatorio";
        }
        return msg;
    }

    /**
     * Método para validar campos numéricos inteiros
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param com o valor a ser convertido para integer.
     * @return String com mensagem de erro ou vazia
     */
    public static String validaInteger(String nomeCampo, String param) {
        String msg = "";
        if (!param.matches("\\d*")) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_numerico";
        }
        return msg;
    }
    /**
     * Método para validar se o DDD e o primeiro número do telefone estão de acordo com o padrão brasileiro
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param com o valor a ser testado
     * @return String com mensagem de erro ou vazia
     */
    public static String validaNumeroDDD(String nomeCampo, String param) {
        String msg = "";
        if (!param.matches("(10)|([1-9][1-9])[2-9]\\d{3,4}\\d{4}")) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_ddd";
        }
        return msg;
    }
    /**
     * Método para validar campo Matricula por tamanho
     *
     * @param tamanhoMin tamanho mínimo do campo.
     * * @param tamanhoMax tamanho máximo do campo.
     * @param param valor do texto a ser testado.
     * @return String com mensagem de erro ou vazia
     */
    public static String validaTamanhoMatricula(int tamanhoMin, int tamanhoMax, String param) {
        String msg = "";
        if (param != null && (param.length() < tamanhoMin || param.length() > tamanhoMax)) {
            msg = "br.cefetrj.sisgee.resources.form.matricula_aluno_tam";
        }
        return msg;
    }

    /**
     * Método para validar campos numéricos de ponto flutuante
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param String com o valor a ser convertido para float.
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaFloat(String nomeCampo, String param) {
        String msg = "";

        try {
            @SuppressWarnings("unused")
            Float valorFloat = Float.parseFloat(param);

        } catch (Exception e) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_numerico";
        }

        return msg;

        /*NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
		Number n;
		try {
			n = nf.parse(param);
			//double d = n.doubleValue();
			float d = n.floatValue();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			msg = "br.cefetrj.sisgee.valida_utils.msg_valida_numerico";
		}
		
		return msg;
         */
    }

    /**
     * Método para validar campos booleanos
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param String com o valor a ser convertido para boolean.
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaBoolean(String nomeCampo, String param) {
        String msg = "";
        try {
            @SuppressWarnings("unused")
            Boolean valorBoolean = Boolean.parseBoolean(param);
        } catch (Exception e) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_booleano";
        }
        return msg;
    }

    /**
     * Método para validar campos de data (java.util.Date)
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param data a ser convertida para Date
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaDate(String nomeCampo, String param) {
        String msg = "";

        try {
            int mes = Integer.parseInt(param.substring(3, 5));
            int dia = Integer.parseInt(param.substring(0, 2));
            
            if (mes <= 12 && dia <= 31 && mes>0 && dia>0) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                @SuppressWarnings("unused")
                Date dataFormatada = format.parse(param);

            }else{
                msg="br.cefetrj.sisgee.valida_utils.msg_valida_date";
            }

        } catch (Exception e) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_date";
        }
        return msg;
    }

    /**
     * Método para validar duas datas (iní­cio e fim de um perí­odo)
     *
     * @param dataInicio data que marca o iní­cio do período.
     * @param dataFim data que marca o final do perí­odo.
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaDatas(Date dataInicio, Date dataFim) {
        String msg = "";
        if (dataFim == null) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_datas";
            return msg;
        } else if (dataInicio.compareTo(dataFim) > 0) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_datas";
            return msg;
        }
        return msg;

    }

    /**
     * Método para validar a Data Fim Termo Estágio Rescisao
     *
     * @param dataFim data que marca o início do período.
     * @param dataFimRegistro data que marca o final do período.
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaDatasRescisao(Date dataFim, Date dataFimRegistro) {
        String msg = "";
        if (dataFimRegistro == null) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_datas";
            return msg;
        } else if (dataFim.compareTo(dataFimRegistro) < 0) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_datas_rescisao";
            return msg;
        }
        return msg;

    }

    /**
     * Método para validar a seleção de Estados (UFs)
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param texto com a UF
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaUf(String nomeCampo, String param) {
        String msg = "";
        boolean valid = false;
        UF[] ufs = UF.asList();
        for (UF uf : ufs) {
            if (param.equals(uf.getUf())) {
                valid = true;
                return "";
            }
        }
        if (valid == false) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_UF";
        }

        return msg;
    }

    /**
     * Método que combina a validação de campo obrigatório e tamanho de campo
     *
     * @param nomeCampo texto com o nome do campo.
     * @param tamanho tamanho do campo
     * @param param valor do texto a ser testado.
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaObrigatorioETamanho(String nomeCampo, int tamanho, String param) {
        String msg = "";
        msg = validaObrigatorio(nomeCampo, param);
        if (msg.trim().isEmpty()) {
            msg = validaTamanho(nomeCampo, tamanho, param);
        }
        return msg;
    }

    /**
     * Método que confirmar se valor da Bolsa do Estágio é menor que R$2.000,00
     *
     * @param nomeCampo texto com o nome do campo.
     * @param param valor do texto a ser testado.
     * @return String com mensagem de erro ou vazia.
     */
    public static String validaTamanhoFloat(String nomeCampo, float param) {
        String msg = "";
        Float valor = Float.parseFloat("200000");
        if (Float.compare(param, valor) > 0) {
            msg = "br.cefetrj.sisgee.valida_utils.msg_valida_tamanho_num_valor";
        }
        return msg;
    }
}