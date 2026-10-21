
import streamlit as st
import requests
import pandas as pd

# =========================================================
# PAGE CONFIGURATION
# =========================================================
st.set_page_config(
    page_title="Automated Seating Arrangement System",
    page_icon="🎓",
    layout="wide",
    initial_sidebar_state="expanded"
)

API_URL = "https://automated-seating-api.onrender.com"

# =========================================================
# PROFESSIONAL CUSTOM THEME
# =========================================================
st.markdown("""
<style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

    html, body, [class*="css"] {
        font-family: 'Inter', sans-serif;
    }

    .stApp {
        background: linear-gradient(135deg, #f5f7ff 0%, #eef4ff 100%);
    }

    /* Sidebar */
    [data-testid="stSidebar"] {
        background: linear-gradient(180deg, #101c3b 0%, #182e58 100%);
    }

    [data-testid="stSidebar"] * {
        color: #f8fafc !important;
    }

    [data-testid="stSidebar"] hr {
        border-color: #3b5075;
    }

    /* Navigation dropdown box */
    [data-testid="stSidebar"] [data-baseweb="select"] {
        background: #ffffff !important;
        border-radius: 10px !important;
    }

    [data-testid="stSidebar"] [data-baseweb="select"] > div {
        background-color: #ffffff !important;
        border: 1px solid #cbd5e1 !important;
        border-radius: 10px !important;
        box-shadow: none !important;
    }

    /* Selected navigation text: BLACK */
    [data-testid="stSidebar"] [data-baseweb="select"] [class*="singleValue"],
    [data-testid="stSidebar"] [data-baseweb="select"] [class*="ValueContainer"] > div {
        color: #000000 !important;
        -webkit-text-fill-color: #000000 !important;
        opacity: 1 !important;
    }

    /* Dropdown arrow */
    [data-testid="stSidebar"] [data-baseweb="select"] svg {
        color: #000000 !important;
        fill: #000000 !important;
    }

    /* Dropdown options */
    [data-baseweb="popover"] [role="option"] {
        background-color: #ffffff !important;
        color: #000000 !important;
    }

    [data-baseweb="popover"] [role="option"] * {
        color: #000000 !important;
        -webkit-text-fill-color: #000000 !important;
    }

    /* Hovered dropdown option */
    [data-baseweb="popover"] [role="option"]:hover {
        background-color: #dbeafe !important;
        color: #000000 !important;
    }

    /* Headings */
    h1, h2, h3 {
        color: #142447;
        font-weight: 800 !important;
    }

    /* Hero banner */
    .hero {
        background: linear-gradient(115deg, #142447 0%, #2457b2 65%, #3978e8 100%);
        padding: 30px 32px;
        border-radius: 20px;
        color: white;
        margin-bottom: 24px;
        box-shadow: 0 10px 28px rgba(28, 67, 140, 0.18);
    }

    .hero h1 {
        color: white !important;
        font-size: 30px;
        margin-bottom: 8px;
    }

    .hero p {
        color: #e0eaff;
        font-size: 15px;
        margin-bottom: 0;
    }

    /* Section label */
    .section-label {
        color: #52709e;
        font-size: 12px;
        font-weight: 800;
        letter-spacing: 2px;
        text-transform: uppercase;
        margin-bottom: 6px;
    }

    /* Dashboard metrics */
    div[data-testid="stMetric"] {
        background: white;
        padding: 20px;
        border-radius: 16px;
        border: 1px solid #e1e9f7;
        box-shadow: 0 5px 18px rgba(31, 55, 100, 0.06);
    }

    div[data-testid="stMetricLabel"] {
        color: #60708d;
        font-weight: 600;
    }

    div[data-testid="stMetricValue"] {
        color: #173b80;
        font-weight: 800;
    }

    /* Information cards */
    .info-card {
        background: white;
        padding: 20px;
        border-radius: 16px;
        border: 1px solid #e1e9f7;
        box-shadow: 0 5px 18px rgba(31, 55, 100, 0.05);
        min-height: 120px;
        margin-bottom: 10px;
    }

    .info-card h3 {
        font-size: 17px;
        margin-bottom: 8px;
    }

    .info-card p {
        color: #64748b;
        font-size: 13px;
        margin-bottom: 0;
    }

    /* Buttons */
    div.stButton > button {
        border-radius: 10px;
        border: 1px solid #dbe5f5;
        font-weight: 700;
        min-height: 45px;
        transition: all 0.2s ease;
    }

    div.stButton > button:hover {
        border-color: #3878e8;
        color: #2457b2;
        box-shadow: 0 4px 12px rgba(56, 120, 232, 0.12);
    }

    div.stButton > button[kind="primary"],
    div.stFormSubmitButton > button[kind="primary"] {
        background: linear-gradient(100deg, #2457b2, #3978e8);
        color: white;
        border: none;
    }

    /* Data tables */
    div[data-testid="stDataFrame"] {
        border: 1px solid #e1e9f7;
        border-radius: 12px;
        overflow: hidden;
    }

    /* Forms */
    div[data-testid="stForm"] {
        background: rgba(255, 255, 255, 0.9);
        border: 1px solid #e1e9f7;
        padding: 20px;
        border-radius: 16px;
    }

    /* Alerts and dividers */
    [data-testid="stAlert"] {
        border-radius: 12px;
    }

    hr {
        border-color: #dce6f5;
    }

    /* Footer */
    .footer {
        text-align: center;
        color: #8492aa;
        font-size: 12px;
        padding: 24px 0 8px;
    }
</style>
""", unsafe_allow_html=True)


# =========================================================
# HELPER FUNCTIONS
# =========================================================
MENU_OPTIONS = [
    "Home",
    "Student Management",
    "Room Management",
    "Generate Seating Arrangement",
    "View Seating Arrangement"
]


def navigate_to(page):
    """Change the sidebar selection before the next rerun."""
    st.session_state["selected_menu"] = page


def fetch_records(endpoint):
    """Fetch a list of records from the Java API."""
    response = requests.get(f"{API_URL}/{endpoint}", timeout=10)
    response.raise_for_status()
    data = response.json()

    if isinstance(data, list):
        return data

    return data.get(endpoint, [])


def show_connection_error():
    st.error(
        "Cannot connect to the Java backend. "
        "Please start SeatingApiServer and try again."
    )


def show_page_header(title, subtitle, icon):
    st.markdown(
        f"""
        <div class="hero">
            <h1>{icon} {title}</h1>
            <p>{subtitle}</p>
        </div>
        """,
        unsafe_allow_html=True
    )


def show_dataframe(records):
    """Display records and offer a CSV download."""
    df = pd.DataFrame(records)

    st.dataframe(
        df,
        use_container_width=True,
        hide_index=True
    )

    st.download_button(
        "⬇️ Download CSV",
        data=df.to_csv(index=False).encode("utf-8"),
        file_name="seating_system_records.csv",
        mime="text/csv"
    )


# =========================================================
# SIDEBAR NAVIGATION
# =========================================================
with st.sidebar:
    st.markdown(
        """
        <div style="text-align:center; padding:18px 4px 22px;">
            <div style="font-size:42px;">🎓</div>
            <div style="font-size:19px;font-weight:800;">
                EXAM SEATING
            </div>
            <div style="font-size:11px;color:#b9c9e7;">
                MANAGEMENT PORTAL
            </div>
        </div>
        """,
        unsafe_allow_html=True
    )

    st.divider()
    st.markdown("### 🧭 Navigation")

    if "selected_menu" not in st.session_state:
        st.session_state["selected_menu"] = "Home"

    menu = st.selectbox(
        "Choose a module",
        MENU_OPTIONS,
        key="selected_menu"
    )

    st.divider()

    st.markdown(
        """
        <div style="font-size:12px;line-height:2;">
            <b>PROJECT MODULES</b><br>
            👨‍🎓 Student Records<br>
            🏫 Room Management<br>
            🎯 Seating Generator<br>
            📋 Seating Reports
        </div>
        """,
        unsafe_allow_html=True
    )

    st.divider()

    st.caption("Automated Seating Arrangement System")
    st.caption("Academic Project Dashboard")


# =========================================================
# TOP HEADER
# =========================================================
st.markdown(
    """
    <div style="display:flex;justify-content:space-between;
                align-items:center;margin-bottom:16px;">
        <div>
            <div style="color:#60708d;font-size:12px;
                        font-weight:700;letter-spacing:1.5px;">
                EXAMINATION MANAGEMENT
            </div>
            <div style="color:#142447;font-size:23px;
                        font-weight:800;">
                Automated Seating Arrangement System
            </div>
        </div>
    </div>
    """,
    unsafe_allow_html=True
)


# =========================================================
# HOME DASHBOARD
# =========================================================
if menu == "Home":
    show_page_header(
        "Exam Seating Dashboard",
        "Manage students, examination rooms and seating allocations in one place.",
        "🎓"
    )

    student_count = 0
    room_count = 0
    allocation_count = 0

    students_loaded = False
    rooms_loaded = False
    allocations_loaded = False

    try:
        students = fetch_records("students")
        student_count = len(students)
        students_loaded = True
    except requests.exceptions.RequestException:
        st.warning("Student statistics are temporarily unavailable.")
    except (ValueError, TypeError):
        st.warning("Student statistics could not be read.")

    try:
        rooms = fetch_records("rooms")
        room_count = len(rooms)
        rooms_loaded = True
    except requests.exceptions.RequestException:
        st.warning("Room statistics are temporarily unavailable.")
    except (ValueError, TypeError):
        st.warning("Room statistics could not be read.")

    try:
        allocations = fetch_records("allocations")
        allocation_count = len(allocations)
        allocations_loaded = True
    except requests.exceptions.RequestException:
        st.warning("Seating statistics are temporarily unavailable.")
    except (ValueError, TypeError):
        st.warning("Seating statistics could not be read.")

    st.markdown(
        '<div class="section-label">OVERVIEW AT A GLANCE</div>',
        unsafe_allow_html=True
    )

    c1, c2, c3 = st.columns(3)

    with c1:
        st.metric(
            "👨‍🎓 Total Students",
            student_count if students_loaded else "—"
        )

    with c2:
        st.metric(
            "🏫 Total Rooms",
            room_count if rooms_loaded else "—"
        )

    with c3:
        st.metric(
            "🪑 Seating Allocations",
            allocation_count if allocations_loaded else "—"
        )

    st.write("")
    st.markdown(
        '<div class="section-label">QUICK ACTIONS</div>',
        unsafe_allow_html=True
    )

    q1, q2 = st.columns(2)

    with q1:
        st.markdown(
            """
            <div class="info-card">
                <h3>👨‍🎓 Student Management</h3>
                <p>Add student details and review student records.</p>
            </div>
            """,
            unsafe_allow_html=True
        )

        st.button(
            "Open Student Management →",
            key="quick_students",
            use_container_width=True,
            on_click=navigate_to,
            args=("Student Management",)
        )

        st.markdown(
            """
            <div class="info-card">
                <h3>🎯 Generate Seating</h3>
                <p>Automatically allocate students to available seats.</p>
            </div>
            """,
            unsafe_allow_html=True
        )

        st.button(
            "Generate Seating →",
            key="quick_generate",
            type="primary",
            use_container_width=True,
            on_click=navigate_to,
            args=("Generate Seating Arrangement",)
        )

    with q2:
        st.markdown(
            """
            <div class="info-card">
                <h3>🏫 Room Management</h3>
                <p>Add examination rooms and manage seating capacity.</p>
            </div>
            """,
            unsafe_allow_html=True
        )

        st.button(
            "Open Room Management →",
            key="quick_rooms",
            use_container_width=True,
            on_click=navigate_to,
            args=("Room Management",)
        )

        st.markdown(
            """
            <div class="info-card">
                <h3>📋 View Seating Arrangement</h3>
                <p>Review seating records and export reports to CSV.</p>
            </div>
            """,
            unsafe_allow_html=True
        )

        st.button(
            "View Seating Records →",
            key="quick_view",
            use_container_width=True,
            on_click=navigate_to,
            args=("View Seating Arrangement",)
        )

    st.markdown(
        """
        <div class="footer">
            Automated Seating Arrangement System · Examination Management
        </div>
        """,
        unsafe_allow_html=True
    )


# =========================================================
# STUDENT MANAGEMENT
# =========================================================
elif menu == "Student Management":
    show_page_header(
        "Student Management",
        "Register students and manage examination records.",
        "👨‍🎓"
    )

    st.markdown("### ➕ Register New Student")

    with st.form("add_student_form", clear_on_submit=True):
        c1, c2 = st.columns(2)

        with c1:
            roll_number = st.text_input("Roll Number", placeholder="e.g. CS2026001")
            name = st.text_input("Student Name", placeholder="Enter full name")

        with c2:
            department = st.text_input("Department", placeholder="e.g. CSE")
            semester = st.number_input(
                "Semester", min_value=1, max_value=12, value=1, step=1
            )

        submitted = st.form_submit_button(
            "➕ Add Student",
            type="primary",
            use_container_width=True
        )

        if submitted:
            if not roll_number.strip() or not name.strip() or not department.strip():
                st.warning("Please fill in all student details.")
            else:
                try:
                    response = requests.post(
                        f"{API_URL}/students",
                        json={
                            "rollNumber": roll_number.strip(),
                            "name": name.strip(),
                            "department": department.strip(),
                            "semester": int(semester)
                        },
                        timeout=10
                    )

                    data = response.json()

                    if response.ok and data.get("success"):
                        st.success(
                            data.get("message", "Student added successfully!")
                        )
                        st.rerun()
                    else:
                        st.error(
                            data.get(
                                "error",
                                data.get("message", "Could not add student.")
                            )
                        )

                except requests.exceptions.ConnectionError:
                    show_connection_error()
                except requests.exceptions.RequestException as e:
                    st.error(f"Request failed: {e}")
                except (ValueError, TypeError) as e:
                    st.error(f"Could not process the response: {e}")

    st.divider()
    st.markdown("### 📑 Student Records")

    if st.button("🔄 Refresh Student Records"):
        st.rerun()

    try:
        students = fetch_records("students")

        if students:
            st.caption(f"Total student records: {len(students)}")
            show_dataframe(students)
        else:
            st.info("No student records found.")

    except requests.exceptions.ConnectionError:
        show_connection_error()
    except requests.exceptions.RequestException as e:
        st.error(f"Could not load student records: {e}")
    except (ValueError, TypeError) as e:
        st.error(f"Could not read student records: {e}")


# =========================================================
# ROOM MANAGEMENT
# =========================================================
elif menu == "Room Management":
    show_page_header(
        "Room Management",
        "Configure examination rooms and calculate seating capacity.",
        "🏫"
    )

    st.markdown("### ➕ Register Examination Room")

    with st.form("add_room_form", clear_on_submit=True):
        c1, c2 = st.columns(2)

        with c1:
            room_code = st.text_input("Room Code", placeholder="e.g. R101")
            building = st.text_input("Building Name", placeholder="e.g. Main Block")

        with c2:
            total_rows = st.number_input(
                "Number of Rows", min_value=1, value=5, step=1
            )
            total_columns = st.number_input(
                "Number of Columns", min_value=1, value=5, step=1
            )
            seats_per_bench = st.number_input(
                "Seats Per Bench", min_value=1, value=2, step=1
            )

        capacity = int(total_rows * total_columns * seats_per_bench)

        st.info(f"🪑 Calculated seating capacity: **{capacity} students**")

        submitted = st.form_submit_button(
            "➕ Add Room",
            type="primary",
            use_container_width=True
        )

        if submitted:
            if not room_code.strip() or not building.strip():
                st.warning("Please enter the room code and building name.")
            else:
                try:
                    response = requests.post(
                        f"{API_URL}/rooms",
                        json={
                            "roomCode": room_code.strip(),
                            "building": building.strip(),
                            "totalRows": int(total_rows),
                            "totalColumns": int(total_columns),
                            "seatsPerBench": int(seats_per_bench)
                        },
                        timeout=10
                    )

                    data = response.json()

                    if response.ok and data.get("success"):
                        st.success(
                            data.get("message", "Room added successfully!")
                        )
                        st.rerun()
                    else:
                        st.error(data.get("error", "Could not add room."))

                except requests.exceptions.ConnectionError:
                    show_connection_error()
                except requests.exceptions.RequestException as e:
                    st.error(f"Request failed: {e}")
                except (ValueError, TypeError) as e:
                    st.error(f"Could not process the response: {e}")

    st.divider()
    st.markdown("### 🏢 Existing Examination Rooms")

    if st.button("🔄 Refresh Room Records"):
        st.rerun()

    try:
        rooms = fetch_records("rooms")

        if rooms:
            st.caption(f"Total registered rooms: {len(rooms)}")
            show_dataframe(rooms)
        else:
            st.info("No room records found. Add your first room above.")

    except requests.exceptions.ConnectionError:
        show_connection_error()
    except requests.exceptions.RequestException as e:
        st.error(f"Could not load rooms: {e}")
    except (ValueError, TypeError) as e:
        st.error(f"Could not read room records: {e}")


# =========================================================
# GENERATE SEATING ARRANGEMENT
# =========================================================
elif menu == "Generate Seating Arrangement":
    show_page_header(
        "Generate Seating Arrangement",
        "Run the Java allocation engine to assign students to examination seats.",
        "🎯"
    )

    st.markdown(
        """
        <div class="info-card">
            <h3>⚙️ Automated Allocation Engine</h3>
            <p>
                Generate seating assignments using the registered student
                records and available examination rooms.
            </p>
        </div>
        """,
        unsafe_allow_html=True
    )

    if st.button(
        "🚀 Generate Seating Arrangement",
        type="primary",
        use_container_width=True
    ):
        try:
            with st.spinner("Generating seating arrangement..."):
                response = requests.post(
                    f"{API_URL}/generate",
                    timeout=30
                )

            data = response.json()

            if response.ok and data.get("success"):
                st.success(
                    data.get("message", "Seating generated successfully!")
                )

                c1, c2, c3, c4 = st.columns(4)

                c1.metric("👨‍🎓 Total Students", data.get("totalStudents", 0))
                c2.metric("✅ Allocated", data.get("allocatedStudents", 0))
                c3.metric("🏫 Rooms Used", data.get("roomsUsed", 0))
                c4.metric(
                    "⚡ Execution Time",
                    f"{data.get('executionTimeMs', 0)} ms"
                )

                allocations = data.get("allocations", [])

                if allocations:
                    st.markdown("### 📋 Generated Seating Details")

                    df = pd.DataFrame(allocations)
                    st.dataframe(
                        df,
                        use_container_width=True,
                        hide_index=True
                    )

                    st.download_button(
                        "⬇️ Download Seating as CSV",
                        data=df.to_csv(index=False).encode("utf-8"),
                        file_name="exam_seating_arrangement.csv",
                        mime="text/csv"
                    )
                else:
                    st.warning("No allocation details were returned.")
            else:
                st.error(data.get("error", "Seating generation failed."))

        except requests.exceptions.ConnectionError:
            show_connection_error()
        except requests.exceptions.Timeout:
            st.error("The seating request timed out. Please try again.")
        except requests.exceptions.RequestException as e:
            st.error(f"Request failed: {e}")
        except (ValueError, TypeError) as e:
            st.error(f"Could not process the response: {e}")


# =========================================================
# VIEW SEATING ARRANGEMENT
# =========================================================
elif menu == "View Seating Arrangement":
    show_page_header(
        "View Seating Arrangement",
        "Review generated seat allocations and export examination reports.",
        "📋"
    )

    c1, c2 = st.columns([3, 1])

    with c1:
        st.markdown("### 🪑 Seating Allocation Records")

    with c2:
        refresh = st.button(
            "🔄 Refresh Records",
            use_container_width=True
        )

    try:
        allocations = fetch_records("allocations")

        if allocations:
            df = pd.DataFrame(allocations)

            st.metric(
                "Total Seating Allocations",
                len(df)
            )

            st.dataframe(
                df,
                use_container_width=True,
                hide_index=True
            )

            st.download_button(
                "⬇️ Download Seating Arrangement CSV",
                data=df.to_csv(index=False).encode("utf-8"),
                file_name="seating_arrangement.csv",
                mime="text/csv",
                type="primary"
            )

        else:
            st.info(
                "No seating allocations found. "
                "Open Generate Seating Arrangement to create them."
            )

            if st.button(
                "🎯 Go to Generate Seating",
                on_click=navigate_to,
                args=("Generate Seating Arrangement",)
            ):
                pass

    except requests.exceptions.ConnectionError:
        show_connection_error()
    except requests.exceptions.RequestException as e:
        st.error(f"Could not load seating arrangements: {e}")
    except (ValueError, TypeError) as e:
        st.error(f"Could not read seating arrangements: {e}")


# =========================================================
# FOOTER
# =========================================================
if menu != "Home":
    st.markdown(
        """
        <div class="footer">
            Automated Seating Arrangement System · Examination Management
        </div>
        """,
        unsafe_allow_html=True
    )
