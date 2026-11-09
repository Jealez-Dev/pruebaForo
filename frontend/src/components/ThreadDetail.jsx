import CommentSection from './CommentSection';
import VoteControl from './VoteControl';

const dateFormatter = new Intl.DateTimeFormat('es-ES', {
  dateStyle: 'long',
  timeStyle: 'short'
});

export default function ThreadDetail({ thread, onClose }) {
  if (!thread) return null;

  return (
    <article className="thread-page" aria-label={`Hilo: ${thread.titulo}`}>
      <button className="back-link" type="button" onClick={onClose}>← Volver a explorar</button>
      <div className="thread-page-header">
        <div>
          <p className="eyebrow">Hilo seleccionado</p>
          <span className="badge">{thread.categoria}</span>
          <h1>{thread.titulo}</h1>
          <p className="thread-meta">Publicado por {thread.autor} · {dateFormatter.format(new Date(thread.fechaCreacion))}</p>
        </div>
        <VoteControl targetType="thread" targetId={thread.id} />
      </div>
      <p className="detail-copy">{thread.contenido}</p>
      <div className="detail-rule" />
      <CommentSection threadId={thread.id} />
    </article>
  );
}
