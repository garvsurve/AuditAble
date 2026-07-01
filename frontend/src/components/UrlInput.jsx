import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Loader2 } from 'lucide-react';
import { scanUrl } from '../services/api';

const UrlInput = () => {
  const [url, setUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!url) return;

    try {
      new URL(url.startsWith('http') ? url : `https://${url}`);
    } catch (_) {
      setError('Please enter a valid URL. Example: example.com');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const targetUrl = url.startsWith('http') ? url : `https://${url}`;
      const result = await scanUrl(targetUrl);
      navigate('/report', { state: { result, url: targetUrl } });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to scan URL. Please ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ width: '100%', maxWidth: '800px', margin: '0 auto' }}>
      <form onSubmit={handleSubmit} style={{ position: 'relative' }}>
        <div style={{ position: 'relative', display: 'flex', alignItems: 'flex-end', width: '100%', gap: '1rem' }}>
          
          <div style={{ flex: 1, position: 'relative' }}>
            <label style={{ display: 'block', fontFamily: 'Inter, sans-serif', fontSize: '0.75rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: '0.5rem' }}>Target Publication</label>
            <div style={{ position: 'relative' }}>
              <div style={{ position: 'absolute', left: '0', bottom: '12px', color: 'var(--text-primary)' }}>
                <Search size={24} strokeWidth={1.5} />
              </div>
              
              <input
                type="text"
                className="news-input sharp-corners"
                value={url}
                onChange={(e) => { setUrl(e.target.value); setError(null); }}
                placeholder="https://example.com"
                disabled={loading}
                style={{
                  paddingLeft: '40px',
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  fontFamily: 'Lora, serif'
                }}
              />
            </div>
          </div>
          
          <button
            type="submit"
            className="news-button sharp-corners hard-shadow-hover"
            disabled={loading || !url}
            style={{
              padding: '16px 32px',
              opacity: loading || !url ? 0.5 : 1,
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              height: '58px', // Matches input height approximately
              flexShrink: 0
            }}
          >
            {loading ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                <span>PROCESSING</span>
              </>
            ) : (
              <span>ANALYZE</span>
            )}
          </button>
        </div>
      </form>
      
      {error && (
        <div
          className="sharp-corners"
          style={{
            marginTop: '1.5rem',
            padding: '16px',
            backgroundColor: '#FFF5F5',
            color: 'var(--accent-primary)',
            border: '1px solid var(--accent-primary)',
            borderLeft: '4px solid var(--accent-primary)',
            fontSize: '0.9rem',
            fontFamily: 'Inter, sans-serif',
            fontWeight: 500
          }}
        >
          <span style={{ fontWeight: 'bold', marginRight: '8px' }}>ERROR:</span>
          {error}
        </div>
      )}
    </div>
  );
};

export default UrlInput;
