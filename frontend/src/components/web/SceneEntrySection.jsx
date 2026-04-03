import { Link } from 'react-router-dom';
import { buildResourceListPath } from '../../utils/attribution';
import styles from './SceneEntrySection.module.css';

function SceneEntrySection({ entries, search }) {
  if (!entries?.length) {
    return null;
  }

  return (
    <section className={styles.section} aria-label="场景直达">
      <div className="section-title">
        <h2>按场景直接进入</h2>
        <p>按老师真实使用场景跳转，不用先自己想该点哪个分类。</p>
      </div>
      <div className={styles.grid}>
        {entries.map((entry) => (
          <Link
            key={entry.title}
            to={buildResourceListPath(search, entry.filters)}
            className={styles.card}
          >
            <span className={styles.eyebrow}>{entry.eyebrow}</span>
            <h3>{entry.title}</h3>
            <p>{entry.description}</p>
          </Link>
        ))}
      </div>
    </section>
  );
}

export default SceneEntrySection;
