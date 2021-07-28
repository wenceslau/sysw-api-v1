
package com.suite.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suite.core.base.ServiceCore;
import com.suite.core.model.Notify;
import com.suite.core.model.Sector;
import com.suite.core.model.User;
import com.suite.core.repository.NotifyRepository;

/**
 * Service responsavel por manipular os dados recurso Notify
 * @author 4931pc_neto
 *
 */
@Service
public class NotifyService extends ServiceCore {

	@Autowired
	private WebSocketService webSocketService;

	@Autowired
	private NotifyRepository notifyRepository;

	/**
	 * Encontra o recurso pelo codigo
	 * @param code
	 * @return
	 */
	public Notify findByCode(Long code) {
		Optional<Notify> optional = notifyRepository.findById(code);

		if (!optional.isPresent())
			throw new EmptyResultDataAccessException("No resource found for code " + code, 1);

		return optional.get();

	}

	/**
	 * Recupera a lista de recursos
	 * @return
	 */
	public List<Notify> list() {
		return notifyRepository.findAll();

	}

	/**
	 * Recupera a lista das ultimas notificacoes
	 * maximo de um 1 dia antes
	 * @return
	 */
	public List<Notify> listLatestNotify() {
		LocalDateTime ldd = LocalDateTime.now().plusDays(-1);

		// Se for o SA ve todas as notificacao de todas as UN
		if (isSa() && isDefaultSector())
			return notifyRepository.findAllByDateTimeNotifyGreaterThanOrderByDateTimeNotifyDesc(ldd);
		else {
			User us = getUser();

			// Se o user nao esta habilitado a receber notificacao, ele nao visualiza nada
			if (!us.getReceiveNotify())
				return Collections.emptyList();

			List<Long> listCodes;

			// se for o SA ou UA ve os dados de todos os setores a qual eles fazem parte
			if (isSa() || isUa()) {
				listCodes = new ArrayList<>(us.getSectors().size());
				for (Sector sec : us.getSectors())
					listCodes.add(sec.getCode());

			} else { // outro perfil somente do setor que esta logado
				listCodes = new ArrayList<>(1);
				listCodes.add(getCodeSectorContext());

			}

			// TODO TROCAR POR NOME NAO USAR O 1L
			// Remove o setor default
			listCodes.removeIf(s -> s.equals(1L));

			return notifyRepository.findAllByDateTimeNotifyGreaterThanAndSectorCodeInOrderByDateTimeNotifyDesc(ldd, listCodes);

		}

	}

	/**
	 * Insere a noficacao enviada no repositorio
	 * @param notify
	 * @return
	 */
	public Notify insert(Notify notify) {
		return notifyRepository.save(notify);

	}

	/**
	 * Envia uma notificacao
	 * @param message
	 */
	public void notify(String message) {
		String type = "DEFAULT";
		User usr = getUser();

		if (usr.getSendNotify() != null && usr.getSendNotify())
			send(message, type, usr, false);

	}

	public void notify(String message, User usr) {
		String type = "DEFAULT";

		if (usr.getSendNotify() != null && usr.getSendNotify())
			send(message, type, usr, false);

	}

	/**
	 * Envia uma notificacao tipada
	 * TODO Criar enum para o tipo de notificacao
	 * @param message
	 * @param type
	 */
	public void notifyUser(String message, String type) {
		User usr = getUser();
		send(message, type, usr, true);

	}

	/**
	 * Metodo interno de envio da notificacao usando o websocket service
	 * @param message
	 * @param type
	 * @param usr
	 * @param forUser
	 */
	private void send(String message, String type, User usr, boolean forUser) {
		Notify notify = new Notify();
		notify.setDateTimeNotify(LocalDateTime.now());
		notify.setMessage(message);
		notify.setUserCode(usr.getCode());
		notify.setUserName(usr.getUsername());
		notify.setType(type);
		Sector sector = null;

		try {
			sector = getSector();

		} catch (Exception e) {}

		if (sector != null) {
			notify.setSectorCode(sector.getCode());
			notify.setSectorName(sector.getName());

		}

		insert(notify);
		debug("Noficação enviada [" + message + "]");

		if (forUser)
			webSocketService.notifyUser(notify);
		else
			webSocketService.notify(notify);

	}

}
