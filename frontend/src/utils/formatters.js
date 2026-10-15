// Indian Currency Formatter (strict adherence to INR standard)
const inrFormatter = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

export const formatCurrency = (amount) => {
  if (amount === undefined || amount === null || isNaN(amount)) {
    return inrFormatter.format(0);
  }
  return inrFormatter.format(Number(amount));
};

export const formatQuantity = (litres, precision = 2) => {
  if (litres === undefined || litres === null || isNaN(litres)) {
    return '0.00 L';
  }
  return `${Number(litres).toFixed(precision)} L`;
};

export const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  });
};

export const formatMonthYear = (month, year) => {
  if (!month || !year) return '';
  const date = new Date(year, month - 1, 1);
  return date.toLocaleDateString('en-IN', { month: 'long', year: 'numeric' });
};
