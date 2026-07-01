import React from 'react';
import UrlInput from '../components/UrlInput';
import ThemeToggle from '../components/ThemeToggle';

const Home = () => {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      minHeight: '100vh',
    }}>
      {/* Newspaper Header */}
      <header style={{
        borderBottom: '4px solid var(--border-color)',
        padding: '1rem 2rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        fontFamily: 'Inter, sans-serif',
        textTransform: 'uppercase',
        fontSize: '0.85rem',
        fontWeight: 600,
        letterSpacing: '0.05em'
      }}>
        <div style={{ width: '100px' }}></div> {/* Spacer for centering */}
        <span style={{ fontWeight: 700 }}>AUDITABLE</span>
        <div style={{ width: '100px', display: 'flex', justifyContent: 'flex-end' }}>
          <ThemeToggle />
        </div>
      </header>

      <main style={{
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        padding: '4rem 1rem',
      }} className="newsprint-texture">
        
        <div style={{ textAlign: 'center', marginBottom: '4rem', maxWidth: '800px', width: '100%' }}>

          
          <h1 style={{ marginBottom: '1.5rem', marginTop: 0 }}>
            ACCESSIBILITY<br/>ANALYZER
          </h1>
          
          <div style={{ 
            height: '2px', 
            backgroundColor: 'var(--border-color)', 
            width: '100px', 
            margin: '0 auto 1.5rem auto' 
          }} />
          
          <p className="font-body" style={{ 
            color: 'var(--text-secondary)', 
            fontSize: '1.25rem',
            lineHeight: '1.8',
            maxWidth: '650px',
            margin: '0 auto'
          }}>
            <strong>S</strong>can your website instantly for accessibility issues. Get highly detailed AI-powered insights to ensure your site is usable for everyone.
          </p>
        </div>

        <div style={{ width: '100%', marginBottom: '4rem' }}>
          <UrlInput />
        </div>

        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
          width: '100%',
          maxWidth: '1000px',
          borderTop: '2px solid var(--border-color)',
          borderLeft: '1px solid var(--border-color)',
        }}>
          {[
            { title: 'WCAG Compliance', desc: 'Validates against global accessibility standards. Rigorous checks against the standard.' },
            { title: 'AI Remediation', desc: 'Smart remediation hints for complex issues. Employs advanced neural nets.' },
            { title: 'Detailed Reports', desc: 'Download comprehensive PDF breakdowns. The paper trail you need.' },
          ].map((feature, i) => (
            <div key={i} className="hard-shadow-hover" style={{
              padding: '2rem',
              textAlign: 'left',
              borderRight: '1px solid var(--border-color)',
              borderBottom: '1px solid var(--border-color)',
              backgroundColor: 'var(--bg-primary)'
            }}>
              <h3 className="font-serif" style={{ fontSize: '1.5rem', marginBottom: '0.75rem' }}>{feature.title}</h3>
              <p className="font-body" style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', margin: 0 }}>{feature.desc}</p>
            </div>
          ))}
        </div>
      </main>
      
      <footer style={{
        borderTop: '1px solid var(--border-color)',
        padding: '1.5rem 2rem',
        textAlign: 'center',
        fontFamily: 'Inter, sans-serif',
        textTransform: 'uppercase',
        fontSize: '0.75rem',
        fontWeight: 600,
        letterSpacing: '0.05em'
      }}>
        THE WEB IS FOR EVERYONE • ACCESSIBILITY WITHOUT COMPROMISE
      </footer>
    </div>
  );
};

export default Home;
