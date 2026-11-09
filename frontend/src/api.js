const apiUrl = '/api/hilos';

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options
  });
  if (!response.ok) throw new Error('No se pudo completar la solicitud.');
  return response.status === 204 ? null : response.json();
}

export function searchThreads(query, category) {
  const params = new URLSearchParams();
  if (query.trim()) params.set('q', query.trim());
  if (category) params.set('categoria', category);
  const path = params.size ? `${apiUrl}/buscar?${params}` : apiUrl;
  return request(path);
}

export function getThread(id) {
  return request(`${apiUrl}/${id}`);
}

export function saveThread(thread, id) {
  return request(id ? `${apiUrl}/${id}` : apiUrl, {
    method: id ? 'PUT' : 'POST',
    body: JSON.stringify(thread)
  });
}

export function deleteThread(id) {
  return request(`${apiUrl}/${id}`, { method: 'DELETE' });
}

export function getComments(threadId) {
  return request(`${apiUrl}/${threadId}/comentarios`);
}

export function createComment(threadId, comment) {
  return request(`${apiUrl}/${threadId}/comentarios`, {
    method: 'POST',
    body: JSON.stringify(comment)
  });
}

export function getVoteSummary(targetType, targetId, userId) {
  const resource = targetType === 'thread' ? 'hilos' : 'comentarios';
  return request(`/api/${resource}/${targetId}/voto?usuarioId=${userId}`);
}

export function saveVote(targetType, targetId, userId, state) {
  const resource = targetType === 'thread' ? 'hilos' : 'comentarios';
  return request(`/api/${resource}/${targetId}/voto`, {
    method: 'POST',
    body: JSON.stringify({ usuarioId: userId, estado: state })
  });
}
