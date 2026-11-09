import { useCallback, useEffect, useRef, useState } from 'react';
import SearchBar from './components/SearchBar';
import ThreadDetail from './components/ThreadDetail';
import ThreadList from './components/ThreadList';
import { deleteThread, getThread, saveThread, searchThreads } from './api';

const emptyForm = { titulo: '', contenido: '', autor: '', categoria: 'GENERAL' };

function ThreadForm({ thread, onClose, onSaved }) {
  const [form, setForm] = useState(thread || emptyForm);
  const [error, setError] = useState('');
  const update = (field, value) => setForm((current) => ({ ...current, [field]: value }));

  async function submit(event) {
    event.preventDefault();
    setError('');
    try {
      await saveThread(form, thread?.id);
      onSaved();
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  return (
    <div className="modal-backdrop" role="presentation">
      <form className="thread-form" onSubmit={submit}>
        <div className="form-heading">
          <div><p className="eyebrow">Publicación</p><h2>{thread ? 'Editar hilo' : 'Nuevo hilo'}</h2></div>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Cerrar">×</button>
        </div>
        <label>Título<input value={form.titulo} maxLength="100" onChange={(event) => update('titulo', event.target.value)} required /></label>
        <label>Contenido<textarea value={form.contenido} rows="6" onChange={(event) => update('contenido', event.target.value)} required /></label>
        <label>Autor<input value={form.autor} onChange={(event) => update('autor', event.target.value)} required /></label>
        <label>Categoría
          <select value={form.categoria} onChange={(event) => update('categoria', event.target.value)} required>
            <option value="GENERAL">General</option><option value="DUDAS">Dudas</option><option value="ANUNCIOS">Anuncios</option>
          </select>
        </label>
        <p className="form-error" role="alert">{error}</p>
        <div className="form-actions">
          <button className="button button-muted" type="button" onClick={onClose}>Cancelar</button>
          <button className="button button-primary" type="submit">Guardar hilo</button>
        </div>
      </form>
    </div>
  );
}

export default function App() {
  const [threads, setThreads] = useState([]);
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('');
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');
  const [selectedThread, setSelectedThread] = useState(null);
  const [editingThread, setEditingThread] = useState(null);
  const [detailId, setDetailId] = useState(() => {
    const match = window.location.hash.match(/^#\/hilos\/(\d+)$/);
    return match ? Number(match[1]) : null;
  });
  const debounce = useRef();

  const loadThreads = useCallback(async () => {
    setStatus('Buscando...');
    setError('');
    try {
      setThreads(await searchThreads(query, category));
      setStatus('');
    } catch (requestError) {
      setStatus('');
      setError(requestError.message);
    }
  }, [query, category]);

  useEffect(() => {
    clearTimeout(debounce.current);
    debounce.current = setTimeout(loadThreads, query ? 300 : 0);
    return () => clearTimeout(debounce.current);
  }, [loadThreads, query]);

  async function selectThread(thread) {
    window.location.hash = `/hilos/${thread.id}`;
    setDetailId(thread.id);
    try {
      setSelectedThread(await getThread(thread.id));
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  useEffect(() => {
    const handleHashChange = async () => {
      const match = window.location.hash.match(/^#\/hilos\/(\d+)$/);
      const id = match ? Number(match[1]) : null;
      setDetailId(id);
      if (!id) {
        setSelectedThread(null);
        return;
      }
      try {
        setSelectedThread(await getThread(id));
      } catch (requestError) {
        setSelectedThread(null);
        setError(requestError.message);
      }
    };
    window.addEventListener('hashchange', handleHashChange);
    if (detailId) handleHashChange();
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, []);

  function showThreadList() {
    window.location.hash = '';
    setDetailId(null);
    setSelectedThread(null);
  }

  async function removeThread(id) {
    if (!window.confirm('¿Eliminar este hilo?')) return;
    try {
      await deleteThread(id);
      if (selectedThread?.id === id) showThreadList();
      await loadThreads();
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  return (
    <>
      <header className="site-header">
        <div className="header-inner">
          <a className="brand" href="#" onClick={showThreadList} aria-label="Inicio del foro"><span className="brand-mark">f/</span><span>foro</span></a>
          <nav className="main-nav" aria-label="Navegación principal"><a className="nav-link active" href="#" onClick={showThreadList}>Explorar</a><a className="nav-link" href="#comunidad">Comunidad</a></nav>
          <button className="button button-primary" type="button" onClick={() => setEditingThread({})}>Nuevo hilo <span aria-hidden="true">＋</span></button>
        </div>
      </header>
      <main className={`shell ${detailId ? 'shell-detail' : ''}`}>
        {detailId && selectedThread ? (
          <ThreadDetail thread={selectedThread} onClose={showThreadList} />
        ) : (
          <>
        <section className="page-heading" id="hilos">
          <p className="eyebrow">Comunidad abierta · 2026</p>
          <h1>Conversaciones con intención.</h1>
          <p className="heading-copy">Un espacio para preguntar, compartir y construir ideas juntos.</p>
        </section>
        <div className="layout">
          <div>
            <SearchBar query={query} category={category} onQueryChange={setQuery} onCategoryChange={setCategory} />
            <p className={`status ${error ? 'status-error' : ''}`} role="status">{error || status}</p>
            <ThreadList threads={threads} onEdit={setEditingThread} onDelete={removeThread} onSelect={selectThread} />
          </div>
          <div id="comunidad">
            {selectedThread ? <ThreadDetail thread={selectedThread} onClose={() => setSelectedThread(null)} /> : (
              <aside className="community-panel"><div className="community-banner" /><div className="community-body">
                <div className="community-icon">f/</div><p className="eyebrow">La plaza digital</p><h2>Foro</h2>
                <p>Una comunidad tranquila para intercambiar preguntas, aprendizajes y puntos de vista.</p>
                <dl className="community-stats"><div><dt>Hilos</dt><dd>{threads.length}</dd></div><div><dt>Estado</dt><dd>Abierto</dd></div></dl>
              </div></aside>
            )}
          </div>
        </div>
          </>
        )}
      </main>
      {editingThread && <ThreadForm thread={editingThread.id ? editingThread : null} onClose={() => setEditingThread(null)} onSaved={async () => { setEditingThread(null); await loadThreads(); }} />}
    </>
  );
}
