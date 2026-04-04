import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Loader2 } from 'lucide-react';
import { motion } from 'framer-motion';
import { scanUrl } from '../services/api';

const UrlInput = () => {
  const [url, setUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!url) return;

    // Basic URL validation
    try {
      new URL(url.startsWith('http') ? url : `https://${url}`);
    } catch (_) {
      setError('Please enter a valid URL');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const targetUrl = url.startsWith('http') ? url : `https://${url}`;
      // Call our API service
      const result = await scanUrl(targetUrl);
      
      // Navigate to report page and pass the result as state
      navigate('/report', { state: { result, url: targetUrl } });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to scan URL. Please ensure the backend is running and the URL is accessible.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ width: '100%', maxWidth: '600px', margin: '0 auto' }}>
      <form onSubmit={handleSubmit} style={{ position: 'relative' }}>
        <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
          <div style={{ position: 'absolute', left: '20px', color: 'var(--text-tertiary)' }}>
            <Search size={24} />
          </div>
          <input
            type="text"
            value={url}
            onChange={(e) => { setUrl(e.target.value); setError(null); }}
            placeholder="Enter website URL (e.g., https://example.com)"
            disabled={loading}
            style={{
              width: '100%',
              padding: '20px 20px 20px 60px',
              fontSize: '1.25rem',
              backgroundColor: 'rgba(20, 20, 22, 0.6)',
              border: '1px solid var(--border-color)',
              borderRadius: 'var(--border-radius-lg)',
              color: 'var(--text-primary)',
              backdropFilter: 'blur(12px)',
              outline: 'none',
              transition: 'all 0.3s ease',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.4)'
            }}
            onFocus={(e) => e.target.style.borderColor = 'var(--accent-primary)'}
            onBlur={(e) => e.target.style.borderColor = 'var(--border-color)'}
          />
          <button
            type="submit"
            disabled={loading || !url}
            style={{
              position: 'absolute',
              right: '10px',
              padding: '12px 24px',
              backgroundColor: 'var(--accent-primary)',
              color: 'white',
              border: 'none',
              borderRadius: 'var(--border-radius-md)',
              fontSize: '1rem',
              fontWeight: '600',
              cursor: loading || !url ? 'not-allowed' : 'pointer',
              opacity: loading || !url ? 0.7 : 1,
              transition: 'all 0.2s',
              display: 'flex',
              alignItems: 'center',
              gap: '8px'
            }}
          >
            {loading ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                <span>Scanning</span>
              </>
            ) : (
              'Analyze'
            )}
          </button>
        </div>
      </form>
      
      {error && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          style={{
            marginTop: '16px',
            padding: '12px 16px',
            backgroundColor: 'var(--danger-bg)',
            color: 'var(--danger-color)',
            borderRadius: 'var(--border-radius-sm)',
            border: '1px solid rgba(239, 68, 68, 0.2)',
            fontSize: '0.9rem',
            textAlign: 'center'
          }}
        >
          {error}
        </motion.div>
      )}
    </div>
  );
};

export default UrlInput;
