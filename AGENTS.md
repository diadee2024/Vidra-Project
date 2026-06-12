# AGENTS.md

## Cursor Cloud specific instructions

### What this repository is

This is a **documentation / showcase repository** for the Vidra Project (the
`BoxVidra` and `XboxVidra` Android/Termux emulators). It contains **no
application source code, no build system, no automated tests, and no package
manifest**. The tracked content is:

- `README.md` plus translated copies under `Vidra Project - Repository Languages/`
- Logo PNGs under `Vidra Project - Documents/`
- A `LICENSE` file

The emulators themselves are Android apps distributed via Termux (see `README.md`)
and are marked "Not Available" / "Will be available" — there is nothing to build,
run, lint, or test as a conventional application in this repo.

### Lint / test / build / run

- **Build / test / lint:** none exist. There is no toolchain to install for the
  repo's own content, so the startup update script is intentionally a no-op.
- **The "application" is the rendered documentation.** To preview it exactly as
  GitHub renders it (GitHub-flavored markdown + the logo images), use
  [`grip`](https://github.com/joeyespo/grip):

  ```bash
  python3 -m venv /tmp/preview-venv
  /tmp/preview-venv/bin/pip install grip
  cd /workspace && /tmp/preview-venv/bin/grip README.md 0.0.0.0:6419
  ```

  Then open `http://127.0.0.1:6419/`. grip serves the logo images relative to the
  repo root, so the README displays with all logos and tables.

### Gotchas

- `grip` renders through GitHub's public API; an unauthenticated session can be
  rate-limited. If rendering fails, set `GRIP_GITHUB_API_TOKEN` (or pass
  `--pass <token>`), or use `grip --export` to produce a static HTML file once.
- File and directory names contain **spaces, parentheses, commas, and non-Latin
  characters** (e.g. `Vidra Project - Repository Languages/`). Always quote paths
  in shell commands.
- `python3 -m venv` requires the `python3-venv` apt package, which is **not**
  preinstalled on the base image. Install it with
  `sudo apt-get update && sudo apt-get install -y python3-venv` before creating a
  venv. This is only needed for the optional README preview, not for the repo itself.
