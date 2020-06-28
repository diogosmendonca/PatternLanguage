package br.cefetrj.sisgee.view.termoaditivo;

import br.cefetrj.sisgee.control.AlunoServices;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.cefetrj.sisgee.control.ProfessorOrientadorServices;
import br.cefetrj.sisgee.control.TermoAditivoServices;
import br.cefetrj.sisgee.control.TermoEstagioServices;
import br.cefetrj.sisgee.model.entity.Aluno;
import br.cefetrj.sisgee.model.entity.ProfessorOrientador;
import br.cefetrj.sisgee.model.entity.TermoAditivo;
import br.cefetrj.sisgee.model.entity.TermoEstagio;
import br.cefetrj.sisgee.view.utils.ServletUtils;
import br.cefetrj.sisgee.view.utils.UF;
import br.cefetrj.sisgee.view.utils.ValidaUtils;
import java.text.DateFormat;
import java.util.Calendar;
import jdk.nashorn.internal.runtime.ParserException;

/**
 * Servlet para trazer os dados do banco para a tela de cadastro de Termo
 * Aditivo.
 * 
 * @author Vinicius Paradellas
 * @since 1.1
 *
 */
@WebServlet("/FormTermoAditivoServlet")
public class FormTermoAditivoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{

		Locale locale = ServletUtils.getLocale(request);
		ResourceBundle messages = ResourceBundle.getBundle("Messages", locale);
                Aluno aluno=AlunoServices.buscarAlunoByMatricula(request.getParameter("alMatricula"));
                final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                final Calendar cal = Calendar.getInstance();
		
                /** campos de Vigência */
		String dataFimTermoAditivo = request.getParameter("dataFimTermoEstagio");
                
                /** campos de Carga Horária */
		String cargaHorariaTermoAditivo = request.getParameter("cargaHorariaTermoEstagio");
                
                /** campos de Valor Bolsa */
                
		String valorBolsaTermoAditivo =request.getParameter("valorBolsa");
		if(valorBolsaTermoAditivo!=null){
                    valorBolsaTermoAditivo=valorBolsaTermoAditivo.replace(".", "");
                    valorBolsaTermoAditivo=valorBolsaTermoAditivo.replace(",", ".");
                }
		/** campos de endereço */
		String enderecoTermoAditivo = request.getParameter("enderecoTermoEstagio");
		String numeroEnderecoTermoAditivo = request.getParameter("numeroEnderecoTermoEstagio");
		String complementoEnderecoTermoAditivo = request.getParameter("complementoEnderecoTermoEstagio");
		String bairroEnderecoTermoAditivo = request.getParameter("bairroEnderecoTermoEstagio");
		String cepEnderecoTermoAditivo = request.getParameter("cepEnderecoTermoEstagio");
		String cidadeEnderecoTermoAditivo = request.getParameter("cidadeEnderecoTermoEstagio");
		String estadoEnderecoTermoAditivo = request.getParameter("estadoEnderecoTermoEstagio");	
		
                /** campos de Professor */
		String idProfessorOrientador = request.getParameter("idProfessorOrientador");		
		String idTermoEstagio = request.getParameter("idTermoEstagio");
                
                /** campos de Supervisor */
                String eobrigatorio = request.getParameter("eobrigatorio");
                String nomeSupervisor = request.getParameter("nomeSupervisor");
                String cargoSupervisor = request.getParameter("cargoSupervisor");
                
                /**Campos possíveis selecionados para atualização*/
                String showVigencia = request.getParameter("showVigencia");
		String showValorBolsa = request.getParameter("showValorBolsa");
		String showCargaHoraria = request.getParameter("showCargaHoraria");
                String showLocal = request.getParameter("showLocal");
                String showSupervisor = request.getParameter("showSupervisor");
		String showProfessor = request.getParameter("showProfessor");	
		
		
		
		TermoEstagio termoEstagio = null;
		Integer idTermo = null;		
		
		boolean isValid =true;
		String msg ="";
		String campo ="";
		Integer tamanho =0;
		
		Date dataFim = null;
		Float valor = null;
		Integer cargaHoraria = null;
		ProfessorOrientador professorOrientador = null;
		
		/**
		 * Validação do idTermoEstagio
		 */
		campo = "Termo de Estágio";
		msg = ValidaUtils.validaObrigatorio(campo, idTermoEstagio);
		if (msg.trim().isEmpty()) {
			msg = ValidaUtils.validaInteger(campo, idTermoEstagio);
			if (msg.trim().isEmpty()) {
				idTermo = Integer.parseInt(idTermoEstagio);
				termoEstagio = TermoEstagioServices.buscarTermoEstagio(idTermo);			
			}else {
				msg = messages.getString(msg);
				isValid = false;
			}
			
		}else {
			msg = messages.getString(msg);
			isValid = false;
		}
		
		
		/**
		 * Validações dos campos, com base nas opções selecionadas para alteração
		 */
		if(termoEstagio != null) {
			request.setAttribute("termoEstagio", termoEstagio);
			
			/**
			 * Validação de vigência
			 */
			if(showVigencia != null && !showVigencia.trim().isEmpty()) {
				
				campo = "Data de Término";
				Boolean hasDataFim =false;
				String dataFimMsg = "";
				dataFimMsg = ValidaUtils.validaObrigatorio(campo, dataFimTermoAditivo);
                                System.out.println(""+dataFimMsg);
				if(dataFimMsg.trim().isEmpty()) {					
					dataFimMsg = ValidaUtils.validaDate(campo , dataFimTermoAditivo);
					if(dataFimMsg.trim().isEmpty()) {
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
						try {
							dataFim = format.parse(dataFimTermoAditivo);
							request.setAttribute("dataFim", dataFim);
							hasDataFim = true;
						} catch (Exception e) {							
							isValid = false;
						}
					} else {
						dataFimMsg = messages.getString("br.cefetrj.sisgee.valida_utils.msg_valida_obrigatorio");
						request.setAttribute("dataFimMsg", dataFimMsg);
						isValid = false;						
					}
                                        String periodoMsg = "";
                                        periodoMsg = ValidaUtils.validaDatas(termoEstagio.getDataInicioTermoEstagio(), dataFim);
                                        if(!periodoMsg.trim().isEmpty()) {
                                            periodoMsg = messages.getString(periodoMsg);
                                            request.setAttribute("periodoMsg", periodoMsg);
                                            isValid = false;					
                                        }
				}
                                else{
                                   dataFimMsg = messages.getString("br.cefetrj.sisgee.valida_utils.msg_valida_obrigatorio");
                                   request.setAttribute("dataFimMsg", dataFimMsg);
                                    isValid = false;
                                }
				request.setAttribute("hasDataFim", hasDataFim);
				
				
			}
			
			/**
			 * Validação de valor da Bolsa
			 */
			if (showValorBolsa != null && !showValorBolsa.trim().isEmpty() ) {
				String valorBolsaMsg = "";
				campo = "Valor";
				valorBolsaMsg = ValidaUtils.validaObrigatorio(campo, valorBolsaTermoAditivo);
				if (valorBolsaMsg.trim().isEmpty()) {
                                        System.out.println(valorBolsaTermoAditivo);
					valorBolsaMsg = ValidaUtils.validaFloat(campo, valorBolsaTermoAditivo);
					if (valorBolsaMsg.trim().isEmpty()){
                                                valor = Float.parseFloat(valorBolsaTermoAditivo);
                                                if(valor<=2000){
                                                    request.setAttribute("valor", valor);
                                                }
                                                else{
                                                    request.setAttribute("valorBolsaMsg", "Valor deve ser menor que 2mil reais");
                                                    isValid = false;
                                                }
                                                    
					} else {
						valorBolsaMsg = messages.getString(valorBolsaMsg);
						request.setAttribute("valorBolsaMsg", valorBolsaMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(valorBolsaMsg);
					}
				} else {
					valorBolsaMsg = messages.getString(valorBolsaMsg);
					request.setAttribute("valorBolsaMsg", valorBolsaMsg);
					isValid = false;
					//TODO Fazer log
					System.out.println(valorBolsaMsg);
				}	
			}
			
			/**
			 * Validação de Carga Horária
			 */
			if (showCargaHoraria != null && !showCargaHoraria.trim().isEmpty()) {
				String cargaHorariaMsg = "";
				campo = "Horas por dia";
				tamanho = 6;		
				cargaHorariaMsg = ValidaUtils.validaObrigatorio(campo , cargaHorariaTermoAditivo);
				if (cargaHorariaMsg.trim().isEmpty()) {
					cargaHorariaMsg = ValidaUtils.validaInteger(campo, cargaHorariaTermoAditivo);
					if (cargaHorariaMsg.trim().isEmpty()) {
						cargaHoraria = Integer.parseInt(cargaHorariaTermoAditivo);
						if (cargaHorariaMsg.trim().isEmpty()) {
							cargaHorariaMsg = ValidaUtils.validaTamanho(campo, tamanho, cargaHoraria);
							if (cargaHorariaMsg.trim().isEmpty()) {
							request.setAttribute("cargaHoraria", cargaHoraria);
							}else {
								cargaHorariaMsg = messages.getString(cargaHorariaMsg);
								cargaHorariaMsg = ServletUtils.mensagemFormatada(cargaHorariaMsg, locale, tamanho);
								request.setAttribute("cargaHorariaMsg", cargaHorariaMsg);
							}
						} else {
							cargaHorariaMsg = messages.getString(cargaHorariaMsg);
							request.setAttribute("cargaHorariaMsg", cargaHorariaMsg);
							isValid = false;
							
							
						}
					} else {
						cargaHorariaMsg = messages.getString(cargaHorariaMsg);
						request.setAttribute("cargaHorariaMsg", cargaHorariaMsg);
						isValid = false;
						
					}
				} else {
					cargaHorariaMsg = messages.getString(cargaHorariaMsg);
					request.setAttribute("cargaHorariaMsg", cargaHorariaMsg);
					isValid = false;
				}
			}
			/**
			 * Validação de Professor
			 */
			if (showProfessor != null && !showProfessor.trim().isEmpty()) {
				String idProfessorMsg = "";
				campo = "Professor Orientador";
				Boolean hasProfessor = false;
				idProfessorMsg = ValidaUtils.validaObrigatorio(campo, idProfessorOrientador);
				if (idProfessorMsg.trim().isEmpty()) {
					idProfessorMsg = ValidaUtils.validaInteger(campo, idProfessorOrientador);
					if (idProfessorMsg.trim().isEmpty()) {
						Integer idProfessor = Integer.parseInt(idProfessorOrientador);
						List<ProfessorOrientador> listaProfessores = ProfessorOrientadorServices.listarProfessorOrientador();
						if (listaProfessores != null) {
							if (listaProfessores.contains(new ProfessorOrientador(idProfessor))) {
								professorOrientador = ProfessorOrientadorServices.buscarProfessorOrientador(new ProfessorOrientador(idProfessor));								
								request.setAttribute("idProfessor", idProfessor);
								hasProfessor = true;
							} else {
								idProfessorMsg = messages.getString("br.cefetrj.sisgee.form_termo_estagio_servlet.professor_invalido");
								isValid = false;
								
							}
						} else {
							idProfessorMsg = messages.getString("br.cefetrj.sisgee.form_termo_estagio_servlet.lista_professores_vazia");
							isValid = false;
							//TODO Fazer log
							System.out.println(idProfessorMsg);
						}
					} else {
						idProfessorMsg = messages.getString(idProfessorMsg);
						request.setAttribute("idProfessorMsg", idProfessorMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(idProfessorMsg);
					}
				}
				request.setAttribute("hasProfessor", hasProfessor);
			}
			
			
			/**
			 * Validação de Endereço
			 */
			if (showLocal != null && !showLocal.trim().isEmpty()) {

				/**
				 * Validação do endereço do TermoEstagio usando métodos da Classe ValidaUtils.
				 * Campo obrigatório e tamanho máximo de 255 caracteres.
				 */
				String enderecoMsg = "";
				campo = "Endereço";
				tamanho = 255;
				enderecoMsg = ValidaUtils.validaObrigatorio(campo, enderecoTermoAditivo);
				if(enderecoMsg.trim().isEmpty()) {
					enderecoMsg = ValidaUtils.validaTamanho(campo, tamanho, enderecoTermoAditivo);
					if(enderecoMsg.trim().isEmpty()) {
						request.setAttribute("enderecoTermoEstagio", enderecoTermoAditivo);
					}else {
						enderecoMsg = messages.getString(enderecoMsg);
						enderecoMsg = ServletUtils.mensagemFormatada(enderecoMsg, locale, tamanho);
						request.setAttribute("enderecoMsg", enderecoMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(enderecoMsg);
					}
				}else {
					enderecoMsg = messages.getString(enderecoMsg);
					request.setAttribute("enderecoMsg", enderecoMsg);
					isValid = false;
					//TODO Fazer log
					System.out.println(enderecoMsg);
				}
				
				
				/**
				 * Validação do número do endereço do TermoEstagio usando os métodos da Classe ValidaUtils.
				 * Campo obrigatório e tamanho máximo de 10 caracteres.
				 */
				String numeroEnderecoMsg = "";
				campo = "Número";
				tamanho = 10;
				numeroEnderecoMsg = ValidaUtils.validaObrigatorio(campo , numeroEnderecoTermoAditivo);
				if(numeroEnderecoMsg.trim().isEmpty()) {
					numeroEnderecoMsg = ValidaUtils.validaTamanho(campo, tamanho, numeroEnderecoTermoAditivo);
					if(numeroEnderecoMsg.trim().isEmpty()) {
						request.setAttribute("numeroEnderecoTermoEstagio", numeroEnderecoTermoAditivo);
					}else {				
						numeroEnderecoMsg = messages.getString(numeroEnderecoMsg);
						numeroEnderecoMsg = ServletUtils.mensagemFormatada(numeroEnderecoMsg, locale, tamanho);
						request.setAttribute("numeroEnderecoMsg", numeroEnderecoMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(numeroEnderecoMsg);
					}
				}else {
					numeroEnderecoMsg = messages.getString(numeroEnderecoMsg);
					request.setAttribute("numeroEnderecoMsg", numeroEnderecoMsg);
					isValid = false;
					//TODO Fazer log
					System.out.println(numeroEnderecoMsg);
				}		
				
				/**
				 * Validação do complemento do endereço do TermoEstagio usando os métodos da Classe ValidaUtils.
				 * Campo obrigatório e tamanho máximo de 150 caracteres.
				 */		
				String complementoEnderecoMsg = "";
				campo = "Complemento";
				tamanho = 150;
				complementoEnderecoMsg = ValidaUtils.validaObrigatorio(campo, complementoEnderecoTermoAditivo);
				if(complementoEnderecoMsg.trim().isEmpty()) {
					numeroEnderecoMsg = ValidaUtils.validaTamanho(campo, tamanho, complementoEnderecoTermoAditivo);
					if(complementoEnderecoMsg.trim().isEmpty()) {
						request.setAttribute("complementoEnderecoTermoEstagio", complementoEnderecoTermoAditivo);
					}else {				
						complementoEnderecoMsg = messages.getString(complementoEnderecoMsg);
						complementoEnderecoMsg = ServletUtils.mensagemFormatada(complementoEnderecoMsg, locale, tamanho);
						request.setAttribute("complementoEnderecoMsg", complementoEnderecoMsg);
						isValid = false;
						
					}
				}else {
					complementoEnderecoMsg = messages.getString(complementoEnderecoMsg);
					request.setAttribute("complementoEnderecoMsg", complementoEnderecoMsg);
					isValid = false;					
				}		
				
				/**
				 * Validação do bairro do endereço do TermoEstagio usando métodos da Classe ValidaUtils.
				 * Campo obrigatório e tamanho máximo de 150 caracteres.
				 */
				String bairroEnderecoMsg = "";
				campo = "Bairro";
				tamanho = 150;
				bairroEnderecoMsg = ValidaUtils.validaObrigatorio(campo, bairroEnderecoTermoAditivo);
				if(bairroEnderecoMsg.trim().isEmpty()) {
					bairroEnderecoMsg = ValidaUtils.validaTamanho(campo, tamanho, bairroEnderecoTermoAditivo);
					if(bairroEnderecoMsg.trim().isEmpty()) {
						request.setAttribute("bairroEnderecoTermoEstagio", bairroEnderecoTermoAditivo);
					}else {				
						bairroEnderecoMsg = messages.getString(bairroEnderecoMsg);
						bairroEnderecoMsg = ServletUtils.mensagemFormatada(bairroEnderecoMsg, locale, tamanho);
						request.setAttribute("bairroEnderecoMsg", bairroEnderecoMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(bairroEnderecoMsg);
					}
				}else {
					bairroEnderecoMsg = messages.getString(bairroEnderecoMsg);
					request.setAttribute("bairroEnderecoMsg", bairroEnderecoMsg);
					isValid = false;
					//TODO Fazer log
					System.out.println(bairroEnderecoMsg);
				}			
						
				/**
				 * Validação do cep do endereço do TermoEstagio usando métodos da Classe ValidaUtils.
				 * Campo obrigatório e tamanho máximo de 15 caracteres.
				 */
				String cepEnderecoMsg = "";	
				campo = "CEP";
				tamanho = 15;
				cepEnderecoMsg = ValidaUtils.validaObrigatorio(campo, cepEnderecoTermoAditivo);
				if(cepEnderecoMsg.trim().isEmpty()) {
					cepEnderecoMsg = ValidaUtils.validaTamanho(campo, tamanho, cepEnderecoTermoAditivo);
					if(bairroEnderecoMsg.trim().isEmpty()) {
						request.setAttribute("cepEnderecoTermoEstagio", cepEnderecoTermoAditivo);
					}else {				
						cepEnderecoMsg = messages.getString(cepEnderecoMsg);	
						cepEnderecoMsg = ServletUtils.mensagemFormatada(bairroEnderecoMsg, locale, tamanho);
						request.setAttribute("cepEnderecoMsg", cepEnderecoMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(cepEnderecoMsg);
					}
				}else {
					cepEnderecoMsg = messages.getString(cepEnderecoMsg);
					request.setAttribute("cepEnderecoMsg", cepEnderecoMsg);
					isValid = false;
					//TODO Fazer log
					System.out.println(cepEnderecoMsg);
				}			
				
				
				/**
				 * Validação da Cidade do endereço do TermoEstagio, usando métodos da Classe ValidaUtils.
				 * Campo obrigatório e tamanho máximo de 150 caracteres.
				 */
				String cidadeEnderecoMsg = "";
				campo = "Cidade";
				tamanho = 150;
				cidadeEnderecoMsg = ValidaUtils.validaObrigatorio(campo, cidadeEnderecoTermoAditivo);
				if(cidadeEnderecoMsg.trim().isEmpty()) {
					cidadeEnderecoMsg = ValidaUtils.validaTamanho(campo, tamanho, cidadeEnderecoTermoAditivo);
					if(cidadeEnderecoMsg.trim().isEmpty()) {
						request.setAttribute("cidadeEnderecoTermoEstagio", cidadeEnderecoTermoAditivo);
					}else {
						cidadeEnderecoMsg = messages.getString(cidadeEnderecoMsg);	
						cidadeEnderecoMsg = ServletUtils.mensagemFormatada(cidadeEnderecoMsg, locale, tamanho);
						request.setAttribute("cidadeEnderecoMsg", cidadeEnderecoMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(cidadeEnderecoMsg);
					}
				}else {
					cidadeEnderecoMsg = messages.getString(cidadeEnderecoMsg);	
					request.setAttribute("cidadeEnderecoMsg", cidadeEnderecoMsg);
					isValid = false;
					//TODO Fazer log
					System.out.println(cidadeEnderecoMsg);
				}					
				/**
				 * Validação do Estado do endereço do TermoEstagio, usando métodos da Classe ValidaUtils.
				 * Campo obrigatório e contido na Enum de UFs.
				 */
				String estadoEnderecoMsg = "";
				campo = "Estado";
				estadoEnderecoMsg = ValidaUtils.validaObrigatorio(campo, estadoEnderecoTermoAditivo);
				if(estadoEnderecoMsg.trim().isEmpty()) {
					estadoEnderecoMsg = ValidaUtils.validaUf(campo, estadoEnderecoTermoAditivo);
					if(estadoEnderecoMsg.trim().isEmpty()) {
						request.setAttribute("estadoEnderecoTermoEstagio", estadoEnderecoTermoAditivo);
					}else {
						estadoEnderecoMsg = messages.getString(estadoEnderecoMsg);
						request.setAttribute("estadoEnderecoMsg", estadoEnderecoMsg);
						isValid = false;
						//TODO Fazer log
						System.out.println(estadoEnderecoMsg);
					}
				}else {			
					estadoEnderecoMsg = messages.getString(estadoEnderecoMsg);
					request.setAttribute("estadoEnderecoMsg", estadoEnderecoMsg);
					isValid = false;
					//TODO Fazer log
					System.out.println(estadoEnderecoMsg);
				}
                                
                                /**
                                * Validação do nome do supervisor do TermoEstagio usando métodos da Classe ValidaUtils.
                                * Campo opicional e tamanho máximo de 100 caracteres.
                                */
                                String nomeSupervisorMsg = "";
                                campo = "NomeSupervisor";
                                tamanho = 80;
                                nomeSupervisorMsg = ValidaUtils.validaTamanho(campo, tamanho, nomeSupervisor);
                                if(nomeSupervisorMsg.trim().isEmpty()) {
                                            request.setAttribute("nomeSupervisor", nomeSupervisor);
                                }else {				
                                        nomeSupervisorMsg = messages.getString(nomeSupervisorMsg);
                                        nomeSupervisorMsg = ServletUtils.mensagemFormatada(nomeSupervisorMsg, locale, tamanho);
                                        request.setAttribute("nomeSupervisorMsg", nomeSupervisorMsg);
                                        isValid = false;
                                        //TODO Fazer log
                                        System.out.println(nomeSupervisorMsg);
                                }
			}		
			
			
		}else{
                    
                    isValid = false;
		}	
                
                try{
                if(!(aluno.getTermoEstagios().get(aluno.getTermoEstagios().size()-1).getDataFimTermoEstagio().compareTo(cal.getTime()) ==1)) {
                    
                    
                    isValid=false;
                }
                }catch(ParserException e){
                    isValid=false;
                }
		if (isValid) {
                    
                    
                    
                                if(showVigencia != null && showVigencia.equals("sim")) {
					TermoAditivo termoAditivo1 = new TermoAditivo();
                                        termoAditivo1.setDataFimTermoAditivo(dataFim);
                                        termoAditivo1.setTipoAditivo("Vigência");
                                        
                                        
                                        termoAditivo1.setDataCadastramentoTermoAditivo((cal.getTime()));
                                        termoAditivo1.setTermoEstagio(aluno.getTermoEstagios().get(0));
                                        TermoAditivoServices.incluirTermoAditivo(termoAditivo1);
                                }
				
                                if(showCargaHoraria!=null && showCargaHoraria.equals("sim")) {
                                    System.out.println("Entrou no CargaHorária");
                                    TermoAditivo termoAditivo2 = new TermoAditivo();
                                    termoAditivo2.setCargaHorariaTermoAditivo(cargaHoraria);
                                    termoAditivo2.setTipoAditivo("Carga Horária");
                                    
                                    termoAditivo2.setTermoEstagio(aluno.getTermoEstagios().get(0));
                                    
                                    termoAditivo2.setDataCadastramentoTermoAditivo((cal.getTime()));
                                    TermoAditivoServices.incluirTermoAditivo(termoAditivo2);
                                }
                                
				if(showProfessor != null && showProfessor.equals("sim")) {
                                    TermoAditivo termoAditivo3 = new TermoAditivo();
                                    termoAditivo3.setProfessorOrientador(professorOrientador);
                                    termoAditivo3.setTipoAditivo("Professor Orientador");
                                    
                                    
                                    
                                    termoAditivo3.setDataCadastramentoTermoAditivo((cal.getTime()));
                                    termoAditivo3.setTermoEstagio(aluno.getTermoEstagios().get(0));
                                    TermoAditivoServices.incluirTermoAditivo(termoAditivo3);
                                }
                                
				if(showValorBolsa != null && showValorBolsa.equals("sim")) {
                                    TermoAditivo termoAditivo4 = new TermoAditivo();
                                    termoAditivo4.setValorBolsaTermoAditivo(valor);
                                    termoAditivo4.setTipoAditivo("Valor da Bolsa");
                                    
                                    
                                    termoAditivo4.setDataCadastramentoTermoAditivo((cal.getTime()));
                                    termoAditivo4.setTermoEstagio(aluno.getTermoEstagios().get(0));
                                    TermoAditivoServices.incluirTermoAditivo(termoAditivo4);
				}
				
				if(showLocal != null && showLocal.equals("sim")) {
                                        TermoAditivo termoAditivo5 = new TermoAditivo();
					termoAditivo5.setEnderecoTermoAditivo(enderecoTermoAditivo);
					termoAditivo5.setNumeroEnderecoTermoAditivo(numeroEnderecoTermoAditivo);
					termoAditivo5.setComplementoEnderecoTermoAditivo(complementoEnderecoTermoAditivo);
					termoAditivo5.setBairroEnderecoTermoAditivo(bairroEnderecoTermoAditivo);
					termoAditivo5.setCidadeEnderecoTermoAditivo(cidadeEnderecoTermoAditivo);
					termoAditivo5.setEstadoEnderecoTermoAditivo(estadoEnderecoTermoAditivo);
					termoAditivo5.setCepEnderecoTermoAditivo(cepEnderecoTermoAditivo);
                                        termoAditivo5.setTipoAditivo("Local Estágio");
                                        
                                        
                                        termoAditivo5.setDataCadastramentoTermoAditivo((cal.getTime()));
                                        termoAditivo5.setTermoEstagio(aluno.getTermoEstagios().get(0));
                                        TermoAditivoServices.incluirTermoAditivo(termoAditivo5);
                                        
                                }
                                if(showSupervisor != null && showSupervisor.equals("sim")){
                                    System.out.println("Entrou no Supervisor");
                                    TermoAditivo termoAditivo6 = new TermoAditivo();
                                    termoAditivo6.setEobrigatorio(eobrigatorio);
                                    termoAditivo6.setNomeSupervisor(nomeSupervisor);
                                    termoAditivo6.setCargoSupervisor(cargoSupervisor);
                                    termoAditivo6.setTipoAditivo("Supervisor");
                                    
                                    
                                    termoAditivo6.setDataCadastramentoTermoAditivo((cal.getTime()));
                                    termoAditivo6.setTermoEstagio(aluno.getTermoEstagios().get(0));
                                    TermoAditivoServices.incluirTermoAditivo(termoAditivo6);
                                }
                                
                                
                                String registroAditivoConcluido = messages.getString("br.cefetrj.sisgee.incluir_termo_aditivo_servlet.msg_registroAditivoConcluido");
				request.setAttribute("msg", registroAditivoConcluido);
				
				request.getRequestDispatcher("/index.jsp").forward(request, response);
								
			}
                        else{
                        List<ProfessorOrientador> professores = ProfessorOrientadorServices.listarProfessorOrientador();
			UF[] uf = UF.asList();
                        request.setAttribute("uf", uf);
                        
			/** Dados de aluno*/
                        request.setAttribute("alMatricula", aluno.getMatricula());
                        request.setAttribute("alNome", aluno.getPessoa().getNome());
                        request.setAttribute("alCampus", aluno.getCurso().getCampus().getNomeCampus());
                        request.setAttribute("alCurso", aluno.getCurso());
			
                        /** Dados de convenio*/
                        request.setAttribute("cvNumero", termoEstagio.getConvenio().getNumeroConvenio());
                        if(termoEstagio.getConvenio().getEmpresa()==null){
                            request.setAttribute("cvNome", termoEstagio.getConvenio().getPessoa().getNome());
                            request.setAttribute("tConvenio","pf");
                            request.setAttribute("cvCpfCnpj",termoEstagio.getConvenio().getPessoa().getCpf());
                            request.setAttribute("nomeAgenciada",termoEstagio.getNomeAgenciada());
                            
                        }else{
                            request.setAttribute("cvNome", termoEstagio.getConvenio().getEmpresa().getRazaoSocial());
                            request.setAttribute("tConvenio","pj");
                            request.setAttribute("agIntegracao",termoEstagio.getConvenio().getEmpresa().isAgenteIntegracao());
                            request.setAttribute("cvCpfCnpj", termoEstagio.getConvenio().getEmpresa().getCnpjEmpresa());
                            request.setAttribute("nomeAgenciada",termoEstagio.getNomeAgenciada());
                        }
                        
                        /** Dados de Vigência */
                        request.setAttribute("vidataInicioTermoEstagio",termoEstagio.getDataInicioTermoEstagio2());
                        request.setAttribute("vidataFimTermoEstagio",termoEstagio.getDataFimTermoEstagio2());
                        
                        /** Dados de Carga Horária */
                        request.setAttribute("cacargaHorariaTermoEstagio",termoEstagio.getCargaHorariaTermoEstagio());
                        
                        /** Dados de Valor Bolsa */
                        request.setAttribute("vavalorBolsa",termoEstagio.getValorBolsa());
                        
                        /** Dados de Local */
                        request.setAttribute("enenderecoTermoEstagio",termoEstagio.getEnderecoTermoEstagio());
                        request.setAttribute("ennumeroEnderecoTermoEstagio",termoEstagio.getNumeroEnderecoTermoEstagio());
                        request.setAttribute("encomplementoEnderecoTermoEstagio",termoEstagio.getComplementoEnderecoTermoEstagio());
                        request.setAttribute("enbairroEnderecoTermoEstagio",termoEstagio.getBairroEnderecoTermoEstagio());
                        request.setAttribute("encidadeEnderecoTermoEstagio",termoEstagio.getCidadeEnderecoTermoEstagio());
                        request.setAttribute("enuf",termoEstagio.getEstadoEnderecoTermoEstagio());
                        request.setAttribute("encepEnderecoTermoEstagio",termoEstagio.getCepEnderecoTermoEstagio());
                        
                        /** Dados de Supervisor */
                        
                        request.setAttribute("eobrigatorio",termoEstagio.getEEstagioObrigatorio());
                        request.setAttribute("nomeSupervisor",termoEstagio.getNomeSupervisor());
                        request.setAttribute("cargoSupervisor",termoEstagio.getCargoSupervisor());
                        
                        /** Dados de Professor */
                        request.setAttribute("pfnomeprofessor",termoEstagio.getProfessorOrientador());
                        
                        request.setAttribute("termoEstagio", termoEstagio);
                    
			request.setAttribute("showVigencia", showVigencia);
			request.setAttribute("showCargaHoraria", showCargaHoraria);
			request.setAttribute("showProfessor", showProfessor);
			request.setAttribute("showValorBolsa", showValorBolsa);
			request.setAttribute("showLocal", showLocal);
                        request.setAttribute("showSupervisor", showSupervisor);
			
			msg += "Alguns campos precisam de atenção";
			String aditivo = "sim";
			//request = carregarListas(request);
			request.setAttribute("msg", msg);
			request.setAttribute("aditivo", aditivo);
			
			request.getRequestDispatcher("/form_termo_adciona_aditivo.jsp").forward(request, response);
		}

            }
}