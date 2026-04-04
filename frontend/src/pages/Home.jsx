import React from 'react';
import { motion } from 'framer-motion';
import { ShieldCheck } from 'lucide-react';
import UrlInput from '../components/UrlInput';

const Home = () => {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      padding: '2rem 0'
    }}>
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        style={{ textAlign: 'center', marginBottom: '3rem' }}
      >
        <div style={{ 
          display: 'inline-flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          backgroundColor: 'var(--accent-glow)',
          padding: '16px',
          borderRadius: '50%',
          marginBottom: '1.5rem',
          color: 'var(--accent-primary)',
          boxShadow: '0 0 30px rgba(99, 102, 241, 0.3)'
        }}>
          <ShieldCheck size={48} strokeWidth={1.5} />
        </div>
        <h1 className="text-gradient" style={{ marginBottom: '1rem' }}>
          Accessibility Analyzer
        </h1>
        <p style={{ 
          color: 'var(--text-secondary)', 
          fontSize: '1.2rem',
          maxWidth: '600px',
          margin: '0 auto',
          lineHeight: '1.6'
        }}>
          Scan your website instantly for accessibility issues. Get highly detailed AI-powered insights to ensure your site is usable for everyone.
        </p>
      </motion.div>

      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.6, delay: 0.2 }}
        style={{ width: '100%' }}
      >
        <UrlInput />
      </motion.div>

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.8, delay: 0.6 }}
        style={{
          marginTop: '4rem',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '2rem',
          width: '100%',
          maxWidth: '800px'
        }}
      >
        {[
          { title: 'WCAG Compliance', desc: 'Checks against standard accessibility guidelines' },
          { title: 'AI Suggestions', desc: 'Smart remediation hints for complex issues' },
          { title: 'Detailed Reports', desc: 'Download comprehensive PDF breakdowns' },
        ].map((feature, i) => (
          <div key={i} className="glass-effect" style={{
            padding: '1.5rem',
            borderRadius: 'var(--border-radius-md)',
            textAlign: 'center'
          }}>
            <h3 style={{ fontSize: '1.1rem', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>{feature.title}</h3>
            <p style={{ color: 'var(--text-tertiary)', fontSize: '0.9rem' }}>{feature.desc}</p>
          </div>
        ))}
      </motion.div>
    </div>
  );
};

export default Home;
