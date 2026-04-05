import axios from 'axios';

const BASE_URL = 'http://localhost:7070/api';

/**
 * Scan a URL for accessibility issues
 * @param {string} url - The URL to scan
 * @returns {Promise<Object>} Scan results
 */
export const scanUrl = async (url) => {
  try {
    const response = await axios.post(`${BASE_URL}/scan`, { url });
    return response.data;
  } catch (error) {
    console.error("Error spanning URL:", error);
    throw error;
  }
};

/**
 * Generates URL for downloading the PDF report
 * @param {string} url - The scanned URL
 * @returns {string} PDF endpoint download link
 */
export const getPdfReportUrl = (url) => {
  return `${BASE_URL}/scan/report/pdf?url=${encodeURIComponent(url)}`;
};
