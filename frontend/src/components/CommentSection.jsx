import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { createComment, getComments } from '../api';
import VoteControl from './VoteControl';

const dateFormatter = new Intl.DateTimeFormat('es-ES', { dateStyle: 'medium', timeStyle: 'short' });

function CommentComposer({ threadId, userName, replyTo, content, status, onChange, onCancel, onSubmit }) {
  const inputRef = useRef(null);

  useEffect(() => {
    if (replyTo) inputRef.current?.focus();
  }, [replyTo]);

  return (
    <form className={`comment-form ${replyTo ? 'comment-form-reply' : ''}`} onSubmit={onSubmit}>
      <label className="sr-only" htmlFor={`comment-${threadId}`}>Escribe un comentario</label>
      <textarea
        ref={inputRef}
        id={`comment-${threadId}`}
        value={content}
        onChange={(event) => onChange(event.target.value)}
        placeholder={replyTo ? 'Escribe una respuesta...' : 'Comparte una idea...'}
        rows="3"
      />
      <div className="comment-form-footer">
        <span className="thread-meta">{replyTo ? 'Respuesta anidada' : `Publicando como ${userName}`}</span>
        <div className="comment-form-actions">
          {replyTo && <button className="comment-reply" type="button" onClick={onCancel}>Cancelar</button>}
          <button className="button button-primary button-small" type="submit" disabled={!content.trim() || Boolean(status)}>
            {status || 'Publicar'}
          </button>
        </div>
      </div>
    </form>
  );
}

function CommentItem({ comment, children, onReply, replyTo, composer }) {
  return (
    <article className="comment-item">
      <div className="comment-vote"><VoteControl targetType="comment" targetId={comment.id} compact /></div>
      <div className="comment-body">
        <p className="thread-meta">{comment.autor} · {dateFormatter.format(new Date(comment.fechaCreacion))}</p>
        <p className="comment-copy">{comment.contenido}</p>
        <button className="comment-reply" type="button" onClick={() => onReply(comment.id)}>Responder</button>
        {replyTo === comment.id && composer}
        {children}
      </div>
    </article>
  );
}

export default function CommentSection({ threadId, userId = 1, userName = 'demo' }) {
  const [comments, setComments] = useState([]);
  const [content, setContent] = useState('');
  const [replyTo, setReplyTo] = useState(null);
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');

  const loadComments = useCallback(async () => {
    try {
      setComments(await getComments(threadId));
    } catch (requestError) {
      setError(requestError.message);
    }
  }, [threadId]);

  useEffect(() => { loadComments(); }, [loadComments]);

  const grouped = useMemo(() => {
    const children = new Map();
    comments.forEach((comment) => {
      const parentId = comment.comentarioPadreId ?? null;
      if (!children.has(parentId)) children.set(parentId, []);
      children.get(parentId).push(comment);
    });
    return children;
  }, [comments]);

  async function submit(event) {
    event.preventDefault();
    if (!content.trim()) return;
    setStatus('Publicando...');
    setError('');
    try {
      await createComment(threadId, {
        contenido: content.trim(),
        autor: userName,
        comentarioPadreId: replyTo
      });
      setContent('');
      setReplyTo(null);
      await loadComments();
      setStatus('');
    } catch (requestError) {
      setStatus('');
      setError(requestError.message);
    }
  }

  function renderComments(parentId = null, depth = 0) {
    return (grouped.get(parentId) || []).map((comment) => (
      <CommentItem
        key={comment.id}
        comment={comment}
        onReply={(id) => setReplyTo(id)}
        replyTo={replyTo}
        composer={(
          <CommentComposer
            threadId={threadId}
            userName={userName}
            replyTo={replyTo}
            content={content}
            status={status}
            onChange={setContent}
            onCancel={() => setReplyTo(null)}
            onSubmit={submit}
          />
        )}
      >
        {depth < 4 && <div className="comment-children">{renderComments(comment.id, depth + 1)}</div>}
      </CommentItem>
    ));
  }

  return (
    <section className="comments-section" aria-label="Comentarios">
      <div className="comments-heading">
        <div><p className="eyebrow">Conversación</p><h3>{comments.length} {comments.length === 1 ? 'comentario' : 'comentarios'}</h3></div>
      </div>
      {error && <p className="form-error" role="alert">{error}</p>}
      <div className="comment-list">{renderComments()}</div>
      {!replyTo && (
        <CommentComposer
          threadId={threadId}
          userName={userName}
          replyTo={replyTo}
          content={content}
          status={status}
          onChange={setContent}
          onCancel={() => setReplyTo(null)}
          onSubmit={submit}
        />
      )}
    </section>
  );
}
