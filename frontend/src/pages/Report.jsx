import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, Download, ExternalLink } from 'lucide-react';
import ScoreCard from '../components/ScoreCard';
import CategoryBreakdown from '../components/CategoryBreakdown';
import IssueList from '../components/IssueList';
import ThemeToggle from '../components/ThemeToggle';
import { getPdfReportUrl } from '../services/api';

const Report = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { result, url } = location.state || {};

  useEffect(() => {
    if (!result || !url) {
      navigate('/');
    }
  }, [result, url, navigate]);

  if (!result || !url) return null;

  const [downloading, setDownloading] = useState(false);

  const handleDownload = () => {
    setDownloading(true);
    const pdfUrl = getPdfReportUrl(url);
    window.open(pdfUrl, '_blank');
    setTimeout(() => setDownloading(false), 2000);
  };

  return (
    <div className="newsprint-texture" style={{ minHeight: '100vh', paddingBottom: '4rem' }}>
      {/* Header */}
      <header style={{
        borderBottom: '4px solid var(--border-color)',
        padding: '1rem 2rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor: 'var(--bg-primary)',
        position: 'sticky',
        top: 0,
        zIndex: 40
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <button
            onClick={() => navigate('/')}
            className="news-button-outline"
            style={{
              padding: '8px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              border: '1px solid var(--border-color)',
              cursor: 'pointer'
            }}
          >
            <ArrowLeft size={20} strokeWidth={1.5} />
          </button>
          
          <div>
            <div className="font-mono" style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: '2px' }}>
              Special Report
            </div>
            <a
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              className="font-serif hover:underline"
              style={{
                color: 'var(--text-primary)',
                textDecoration: 'none',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                fontSize: '1.25rem',
                fontWeight: 600
              }}
            >
              {url} <ExternalLink size={16} strokeWidth={1.5} />
            </a>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <ThemeToggle />
          <button
            onClick={handleDownload}
            disabled={downloading}
            className="news-button"
            style={{ display: 'flex', gap: '8px', alignItems: 'center' }}
          >
            {downloading ? (
              <span>PRINTING...</span>
            ) : (
              <><Download size={18} strokeWidth={1.5} /> <span>DOWNLOAD PDF</span></>
            )}
          </button>
        </div>
      </header>

      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '2rem' }}>
        
        <div className="py-8 text-center font-serif text-2xl text-neutral-400 tracking-[1em]" style={{ letterSpacing: '1em', color: 'var(--text-muted)' }}>
          &#x2727; &#x2727; &#x2727;
        </div>

        <h1 style={{ textAlign: 'center', marginBottom: '3rem', fontSize: '5rem' }}>
          ANALYSIS REPORT
        </h1>

        {/* Top Grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
          borderTop: '2px solid var(--border-color)',
          borderLeft: '1px solid var(--border-color)'
        }}>
          <div style={{ borderRight: '1px solid var(--border-color)', borderBottom: '1px solid var(--border-color)' }}>
            <ScoreCard score={result.score !== undefined ? result.score : 0} />
          </div>

          <div style={{ borderRight: '1px solid var(--border-color)', borderBottom: '1px solid var(--border-color)' }}>
            <CategoryBreakdown breakdown={result.breakdown} />
          </div>
        </div>

        {/* Issues List */}
        <div style={{ marginTop: '2rem' }}>
          <IssueList issues={result.issues} />
        </div>
      </div>
    </div>
  );
};

export default Report;
