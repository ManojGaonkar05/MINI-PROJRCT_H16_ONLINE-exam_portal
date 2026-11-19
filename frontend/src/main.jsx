import React, { useState } from 'react';
import { createRoot } from 'react-dom/client';
import './style.css';

const initialForm = {
  createdById: '',
  title: '',
  subject: '',
  durationMinutes: '60',
  startsAt: '',
  endsAt: '',
  totalMarks: '100',
  passingMarks: '40',
};

function App() {
  const [form, setForm] = useState(initialForm);
  const [saving, setSaving] = useState(false);
  const [createdExam, setCreatedExam] = useState(null);
  const [error, setError] = useState('');

  function update(event) {
    setForm(current => ({ ...current, [event.target.name]: event.target.value }));
    setError('');
    setCreatedExam(null);
  }

  async function createExam(event) {
    event.preventDefault();
    setSaving(true);
    setError('');
    setCreatedExam(null);

    const payload = {
      ...form,
      createdById: form.createdById.trim(),
      durationMinutes: Number(form.durationMinutes),
      startsAt: new Date(form.startsAt).toISOString(),
      endsAt: new Date(form.endsAt).toISOString(),
      totalMarks: Number(form.totalMarks),
      passingMarks: Number(form.passingMarks),
    };

    try {
      const response = await fetch('/api/teacher/exams', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      const result = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(result.detail || result.message || 'Could not create the exam. Check the details and try again.');
      }
      setCreatedExam(result);
      setForm(initialForm);
    } catch (submissionError) {
      setError(submissionError.message || 'The API could not be reached. Please try again.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="shell">
      <nav aria-label="Main navigation">
        <a className="brand" href="/">EXAM<span>PORTAL</span></a>
        <span className="workspace-label">Teacher workspace</span>
      </nav>
      <section className="workspace" aria-labelledby="create-title">
        <header className="page-heading">
          <p className="eyebrow">EXAM MANAGEMENT</p>
          <h1 id="create-title">Create an exam</h1>
          <p className="intro">Set the schedule, duration and marks. The exam will be saved as a draft.</p>
        </header>

        <form className="exam-form" onSubmit={createExam}>
          <div className="field field-wide">
            <label htmlFor="createdById">Teacher account ID</label>
            <input id="createdById" name="createdById" value={form.createdById} onChange={update} placeholder="Teacher UUID" required />
            <small>Use the UUID of an active teacher account. Sign-in is not connected yet.</small>
          </div>
          <div className="field">
            <label htmlFor="title">Exam title</label>
            <input id="title" name="title" value={form.title} onChange={update} maxLength="200" required />
          </div>
          <div className="field">
            <label htmlFor="subject">Subject</label>
            <input id="subject" name="subject" value={form.subject} onChange={update} maxLength="120" required />
          </div>
          <div className="field">
            <label htmlFor="durationMinutes">Duration (minutes)</label>
            <input id="durationMinutes" name="durationMinutes" type="number" min="5" max="240" step="1" value={form.durationMinutes} onChange={update} required />
            <small>Choose between 5 and 240 minutes.</small>
          </div>
          <div className="field">
            <label htmlFor="totalMarks">Total marks</label>
            <input id="totalMarks" name="totalMarks" type="number" min="0.01" step="0.01" value={form.totalMarks} onChange={update} required />
          </div>
          <div className="field">
            <label htmlFor="passingMarks">Passing marks</label>
            <input id="passingMarks" name="passingMarks" type="number" min="0" step="0.01" value={form.passingMarks} onChange={update} required />
          </div>
          <div className="field">
            <label htmlFor="startsAt">Start date and time</label>
            <input id="startsAt" name="startsAt" type="datetime-local" value={form.startsAt} onChange={update} required />
          </div>
          <div className="field">
            <label htmlFor="endsAt">End date and time</label>
            <input id="endsAt" name="endsAt" type="datetime-local" value={form.endsAt} onChange={update} required />
          </div>

          <div className="form-actions">
            <button className="button" type="submit" disabled={saving}>
              {saving ? 'Saving…' : 'Create draft exam'}
              <span aria-hidden="true">→</span>
            </button>
          </div>
          {error && <p className="notice error" role="alert">{error}</p>}
          {createdExam && (
            <p className="notice success" role="status">
              Draft exam created. Exam ID: <code>{createdExam.id}</code>
            </p>
          )}
        </form>
      </section>
      <footer><span>Online Exam Portal</span><span>Teacher exam setup</span></footer>
    </main>
  );
}

createRoot(document.getElementById('root')).render(<React.StrictMode><App /></React.StrictMode>);
