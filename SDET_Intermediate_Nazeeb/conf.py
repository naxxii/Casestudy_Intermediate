from rich.console import Console
import pytest

console = Console()

@pytest.hookimpl(tryfirst=True)
def pytest_terminal_summary(terminalreporter, exitstatus, config):
    console.print("[bold green]Test Summary[/bold green]")
    stats = terminalreporter.stats
    console.print(f"[bold yellow]Passed:[/bold yellow] {len(stats.get('passed', []))}")
    console.print(f"[bold red]Failed:[/bold red] {len(stats.get('failed', []))}")
    console.print(f"[bold purple]Skipped:[/bold purple] {len(stats.get('skipped', []))}")
