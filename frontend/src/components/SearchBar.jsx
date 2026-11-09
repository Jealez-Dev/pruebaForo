export default function SearchBar({ query, category, onQueryChange, onCategoryChange }) {
  return (
    <section className="toolbar" aria-label="Filtros de hilos">
      <label className="search-field">
        <span className="sr-only">Buscar hilos</span>
        <span className="search-icon" aria-hidden="true">⌕</span>
        <input
          type="search"
          value={query}
          onChange={(event) => onQueryChange(event.target.value)}
          placeholder="Buscar por título o contenido..."
          autoComplete="off"
        />
      </label>
      <label>
        <span className="sr-only">Filtrar por categoría</span>
        <select value={category} onChange={(event) => onCategoryChange(event.target.value)}>
          <option value="">Todas las categorías</option>
          <option value="GENERAL">General</option>
          <option value="DUDAS">Dudas</option>
          <option value="ANUNCIOS">Anuncios</option>
        </select>
      </label>
    </section>
  );
}
