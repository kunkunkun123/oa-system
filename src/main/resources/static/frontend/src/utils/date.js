export function formatDate(date) {
  if (!date) return '---'
  
  try {
    const d = new Date(date)
    if (isNaN(d.getTime())) return '---'
    
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hour = String(d.getHours()).padStart(2, '0')
    const minute = String(d.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hour}:${minute}`
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '---'
  }
} 