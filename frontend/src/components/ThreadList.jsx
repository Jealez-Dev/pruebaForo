import VoteControl from './VoteControl';

const dateFormatter = new Intl.DateTimeFormat('es-ES', {
  dateStyle: 'medium',
  timeStyle: 'short'
});

export default function ThreadList({ threads, onEdit, onDelete, onSelect }) {
  if (!threads.length) {
    return <p className="empty">No hay hilos que coincidan con la búsqueda.</p>;
  }

  return (
    <section className="thread-list" aria-live="polite">
      {threads.map((thread) => (
        <article className="thread-card" key={thread.id}>
          <VoteControl targetType="thread" targetId={thread.id} compact />
          <button className="thread-content" type="button" onClick={() => onSelect(thread)}>
            <p className="thread-meta">f/foro · {thread.autor} · {dateFormatter.format(new Date(thread.fechaCreacion))}</p>
            <h2>{thread.titulo}</h2>
            <p>{thread.contenido}</p>
            <span className="badge">{thread.categoria}</span>
          </button>
          <div className="thread-actions">
            <button className="button button-muted button-small" onClick={() => onEdit(thread)} type="button">Editar</button>
            <button className="button button-danger button-small" onClick={() => onDelete(thread.id)} type="button">Eliminar</button>
          </div>
        </article>
      ))}
    </section>
  );
}
