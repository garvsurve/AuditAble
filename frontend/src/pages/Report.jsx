import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ArrowLeft, Download, ExternalLink } from 'lucide-react';
import ScoreCard from '../components/ScoreCard';
import CategoryBreakdown from '../components/CategoryBreakdown';
import IssueList from '../components/IssueList';
import { getPdfReportUrl } from '../services/api';

const Report = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { result, url } = location.state || {};

  // Redirect to home if no valid state
  useEffect(() => {
    if (!result || !url) {
      navigate('/');
    }
  }, [result, url, navigate]);

  if (!result || !url) return null;

  const handleDownload = () => {
    const pdfUrl = getPdfReportUrl(url);
    window.open(pdfUrl, '_blank');
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.1 }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
  };

  return (
    <motion.div
      initial="hidden"
      animate="visible"
      variants={containerVariants}
      style={{ padding: '2rem 0', maxWidth: '1200px', margin: '0 auto' }}
    >
      {/* Header */}
      <motion.div variants={itemVariants} style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '2rem',
        flexWrap: 'wrap',
        gap: '1rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <button
            onClick={() => navigate('/')}
            style={{
              background: 'none',
              border: 'none',
              color: 'var(--text-secondary)',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              padding: '8px',
              borderRadius: '50%',
              backgroundColor: 'rgba(255,255,255,0.05)',
              transition: 'all 0.2s'
            }}
            onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.1)'}
            onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.05)'}
          >
            <ArrowLeft size={20} />
          </button>
          <div>
            <h1 style={{ fontSize: '1.75rem', color: 'var(--text-primary)', marginBottom: '4px' }}>Analysis Report</h1>
            <a
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              style={{
                color: 'var(--accent-primary)',
                textDecoration: 'none',
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
                fontSize: '0.9rem'
              }}
            >
              {url} <ExternalLink size={14} />
            </a>
          </div>
        </div>

        <button
          onClick={handleDownload}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            backgroundColor: 'var(--accent-primary)',
            color: 'white',
            border: 'none',
            padding: '10px 20px',
            borderRadius: 'var(--border-radius-sm)',
            fontWeight: '600',
            cursor: 'pointer',
            transition: 'background-color 0.2s',
            boxShadow: '0 4px 14px rgba(99, 102, 241, 0.4)'
          }}
          onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'var(--accent-secondary)'}
          onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'var(--accent-primary)'}
        >
          <Download size={18} /> Download PDF
        </button>
      </motion.div>

      {/* Top Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
        gap: '2rem'
      }}>
        <motion.div variants={itemVariants}>
          <ScoreCard score={result.score !== undefined ? result.score : 0} />
        </motion.div>

        <motion.div variants={itemVariants}>
          <CategoryBreakdown breakdown={result.breakdown} />
        </motion.div>
      </div>

      {/* Issues List */}
      <motion.div variants={itemVariants}>
        <IssueList issues={result.issues} />
      </motion.div>
    </motion.div>
  );
};

export default Report;
